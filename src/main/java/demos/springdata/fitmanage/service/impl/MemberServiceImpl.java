package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdate;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import org.springframework.http.HttpStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.support.MemberSpecification;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {

    private final UserService userService;
    private final UserPasswordService userPasswordService;
    private final UserValidationService userValidationService;
    private final RoleService roleService;
    private final MembershipService membershipService;
    private final VisitService visitService;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    public MemberServiceImpl
            (
                    UserService userService,
                    UserPasswordService userPasswordService,
                    UserValidationService userValidationService,
                    RoleService roleService,
                    ModelMapper modelMapper,
                    MembershipService membershipService,
                    VisitService visitService
            ) {
        this.userService = userService;
        this.userPasswordService = userPasswordService;
        this.userValidationService = userValidationService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.membershipService = membershipService;
        this.visitService = visitService;
    }


    @Transactional
    @Override
    public MemberResponseDto create(CreateUser requestDto) {
        Tenant tenant = userService.getCurrentUser().getTenant();

        User user = buildMember(tenant, requestDto);

        userValidationService.validateGlobalAndTenantScopedCredentials(requestDto.getEmail(), requestDto.getPhone(), tenant.getId());
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
    @Transactional
    public UserResponse deleteMember(Long memberId) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        User user = userService.getByIdAndTenantId(memberId, tenant.getId());

        LOGGER.info("Deleting member with ID {} from tenant {}", memberId, tenant.getName());
        userService.delete(user);
        LOGGER.info("Member with ID {} deleted successfully", memberId);

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .gender(user.getGender())
                .roles(UserRoleHelper.extractRoleTypes(user))
                .birthDate(user.getBirthDate())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .build();
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
    public MemberResponseDto updateMember(Long memberId, MemberUpdate updateRequest) {
        User authenticatedUser = userService.getCurrentUser();
        User member = userService.findUserById(memberId);

        Membership membership = member.getMemberships().stream().findFirst().orElseThrow(() -> new DamilSoftException("Member has no membership created.", HttpStatus.CONFLICT));

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
    public List<MemberTableDto> findMembersTableView() {
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
    public List<MemberTableDto> getMembersByFilter(MemberFilter filter) {
        LOGGER.info("Search for users with filter: {}", filter);

        Tenant tenant = userService.getCurrentUser().getTenant();

        Specification<User> spec = MemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("tenant"), tenant));

        List<User> memberList = userService.findMembersByFilter(spec);

        if (memberList.isEmpty())
            throw new DamilSoftException("No members found for the given filter", HttpStatus.NOT_FOUND);

        Role facilityMemberRole = roleService.findByName(RoleType.MEMBER);

        return memberList
                .stream()
                .filter(user -> user.getRoles().contains(facilityMemberRole))
                .map(this::mapUserToMemberTableDto)
                .toList();
    }

    @Transactional
    @Override
    public List<MemberResponseDto> findMember(MemberFilter filter) {
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

    private User buildMember(Tenant tenant, CreateUser requestDto) {

        User user = modelMapper.map(requestDto, User.class);
        user.setTenant(tenant)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setEnabled(true);

        Role role = roleService.findByName(RoleType.MEMBER);
        user.getRoles().add(role);

        return user;
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

    private List<User> findFirstMemberByFilter(MemberFilter filter) {
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
