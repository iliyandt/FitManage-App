package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.support.MemberSpecification;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.SecurityCodeGenerator;
import demos.springdata.fitmanage.util.UserRoleHelper;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {

    private final UserService userService;
    private final UserPasswordService userPasswordService;
    private final RoleService roleService;
    private final MembershipService membershipService;
    private final VisitService visitService;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    public MemberServiceImpl
            (
                    UserService userService, UserPasswordService userPasswordService,
                    RoleService roleService,
                    ModelMapper modelMapper,
                    MembershipService membershipService,
                    VisitService visitService
            ) {
        this.userService = userService;
        this.userPasswordService = userPasswordService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.membershipService = membershipService;
        this.visitService = visitService;
    }


    @Transactional
    @Override
    public MemberResponseDto createMember(UserCreateRequestDto requestDto) {
        Tenant tenant = userService.getCurrentUser().getTenant();

        User user = buildMember(tenant, requestDto);
        validateCredentials(user, requestDto);

        userPasswordService.setupMemberInitialPassword(user);

        Membership membership = createAndLinkMembershipToUser(tenant, user);

        userService.save(user);
        membershipService.save(membership);

        LOGGER.info("Successfully added member with ID {} to facility '{}'", user.getId(), tenant.getName());

        MemberResponseDto mappedMember = modelMapper.map(user, MemberResponseDto.class).setRoles(UserRoleHelper.extractRoleTypes(user));
        modelMapper.map(membership, mappedMember);

        return mappedMember;
    }

    @Override
    public UserResponseDto removeMember(Long memberId) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        User user = userService.getByIdAndTenantId(memberId, tenant.getId());

        LOGGER.info("Deleting member with ID {} from tenant {}", memberId, tenant.getName());
        userService.delete(user);
        LOGGER.info("Member with ID {} deleted successfully", memberId);

        return new UserResponseDto()
                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setGender(user.getGender())
                .setRoles(UserRoleHelper.extractRoleTypes(user))
                .setBirthDate(user.getBirthDate())
                .setCreatedAt(user.getCreatedAt())
                .setUpdatedAt(user.getUpdatedAt())
                .setPhone(user.getPhone())
                .setAddress(user.getAddress())
                .setCity(user.getCity());
    }

    @Transactional
    @Override
    public MemberResponseDto checkInMember(Long memberId) {
        User user = userService.findUserById(memberId);
        Membership activeMembership = membershipService.getRequiredActiveMembership(user.getMemberships());

        Membership updatedMembership = membershipService.checkIn(activeMembership);

        Visit visit = null;
        if (updatedMembership.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
            visit = visitService.checkIn(updatedMembership, memberId);
        }

        return mapToResponseDto(user, updatedMembership, visit);
    }

    @Override
    public MemberResponseDto updateMemberDetails(Long memberId, MemberUpdateDto updateRequest) {
        User authenticatedUser = userService.getCurrentUser();
        User member = userService.findUserById(memberId);

        Membership membership = member.getMemberships().stream().findFirst().orElseThrow(() -> new FitManageAppException("Member has no membership created.", ApiErrorCode.CONFLICT));

        modelMapper.map(updateRequest, member);
        userService.save(member);

        MemberResponseDto response = modelMapper.map(member, MemberResponseDto.class);

        return response.setSubscriptionPlan(membership.getSubscriptionPlan())
                .setAllowedVisits(membership.getAllowedVisits())
                .setRemainingVisits(membership.getRemainingVisits())
                .setSubscriptionStatus(membership.getSubscriptionStatus())
                .setSubscriptionStartDate(membership.getSubscriptionStartDate())
                .setSubscriptionEndDate(membership.getSubscriptionEndDate())
                .setEmployment(membership.getEmployment())
                .setLastCheckInAt(membership.getLastCheckInAt())
                .setRoles(UserRoleHelper.extractRoleTypes(authenticatedUser));
    }

    @Transactional
    @Override
    public List<MemberTableDto> getAllMembersForTable() {
        LOGGER.info("Prepare list of all members for table view");

        Tenant tenant = userService.getCurrentUser().getTenant();

        List<User> users = tenant.getUsers();
        LOGGER.info("Tenant users size: {}", tenant.getUsers().size());

        return users.stream()
                .filter(user -> !user.getMemberships().isEmpty())
                .map(this::mapUserToMemberTableDto).toList();
    }


    @Transactional
    @Override
    public List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto filter) {
        LOGGER.info("Search for users with filter: {}", filter);

        Tenant tenant = userService.getCurrentUser().getTenant();

        Specification<User> spec = MemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("tenant"), tenant));

        List<User> memberList = userService.findMembersByFilter(spec);

        if (memberList.isEmpty())
            throw new FitManageAppException("No members found for the given filter", ApiErrorCode.NOT_FOUND);

        Role facilityMemberRole = roleService.findByName(RoleType.MEMBER);

        return memberList
                .stream()
                .filter(user -> user.getRoles().contains(facilityMemberRole))
                .map(this::mapUserToMemberTableDto)
                .toList();
    }

    @Transactional
    @Override
    public List<MemberResponseDto> findMember(MemberFilterRequestDto filter) {
        List<User> users = findFirstMemberByFilter(filter);


        return users.stream().map(user -> {
            Membership membership = user.getMemberships().stream()
                    .max(Comparator.comparing(Membership::getCreatedAt))
                    .orElseThrow(() -> new IllegalStateException("User has no memberships"));

            MemberResponseDto dto = mapToResponseDto(user, membership, null);
            dto.setUsername(user.getUsername());
            dto.setRoles(UserRoleHelper.extractRoleTypes(user));
            return dto;
        }).toList();
    }

    private User buildMember(Tenant tenant, UserCreateRequestDto requestDto) {

        User user = modelMapper.map(requestDto, User.class);
        user.setTenant(tenant)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setEnabled(true);

        Role role = roleService.findByName(RoleType.MEMBER);
        user.getRoles().add(role);

        return user;
    }

    //TODO: extract this method
    private void validateCredentials(User user, UserCreateRequestDto requestDto) {
        Map<String, String> errors = new HashMap<>();

        if (userService.existsByEmail(requestDto.getEmail())) {
            LOGGER.warn("User with email {} already exists", requestDto.getEmail());
            errors.put("email", "Email is already registered ");
        }

        if (userService.existsByEmailAndTenant(requestDto.getEmail(), user.getTenant().getId())) {
            LOGGER.warn("User with email {} already exists in tenant with ID: {}", requestDto.getEmail(), user.getTenant().getId());
            errors.put("email", "Email is already registered in this tenant");
        }

        if (userService.existsByPhoneAndTenant(requestDto.getPhone(), user.getTenant().getId())) {
            LOGGER.warn("User with phone {} already exists", user.getPhone());
            errors.put("phone", "Phone used from another user");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }

    private static Membership createAndLinkMembershipToUser(Tenant tenant, User user) {
        Membership membership = new Membership()
                .setTenant(tenant)
                .setUser(user)
                .setSubscriptionStatus(SubscriptionStatus.INACTIVE)
                .setAllowedVisits(0)
                .setRemainingVisits(0);

        user.getMemberships().add(membership);
        return membership;
    }

    private List<User> findFirstMemberByFilter(MemberFilterRequestDto filter) {
        Tenant tenant = userService.getCurrentUser().getTenant();

        LOGGER.warn("Searching users with filter: {}", filter);
        LOGGER.warn("Tenant ID: {}", tenant.getId());

        Specification<User> spec = MemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenant.getId()));

        return userService.findFirstMemberByFilter(spec);
    }

    private MemberResponseDto mapToResponseDto(User user, Membership updatedMembership, Visit visit) {
        MemberResponseDto mappedUser = modelMapper.map(user, MemberResponseDto.class);
        modelMapper.map(updatedMembership, mappedUser);

        if (visit != null) {
            mappedUser.setLastCheckInAt(visit.getCheckInAt());
        }

        return mappedUser;
    }

    private MemberTableDto mapUserToMemberTableDto(User user) {
        MemberTableDto dto = modelMapper.map(user, MemberTableDto.class);

        Membership membership = user.getMemberships().stream()
                .max(Comparator.comparing(Membership::getCreatedAt))
                .orElseThrow(() -> new IllegalStateException("User has no memberships"));

        modelMapper.map(membership, dto);
        dto.setRoles(UserRoleHelper.extractRoleTypes(user));

        return dto;
    }
}
