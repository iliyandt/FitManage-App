package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import org.springframework.http.HttpStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.support.MemberSpecification;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {

    private final UserService userService;
    private final UserPasswordService userPasswordService;
    private final UserValidationService userValidationService;
    private final RoleService roleService;
    private final MembershipService membershipService;
    private final VisitService visitService;
    private final UserMapper userMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    public MemberServiceImpl
            (
                    UserService userService,
                    UserPasswordService userPasswordService,
                    UserValidationService userValidationService,
                    RoleService roleService,
                    MembershipService membershipService,
                    VisitService visitService,
                    UserMapper userMapper
            ) {
        this.userService = userService;
        this.userPasswordService = userPasswordService;
        this.userValidationService = userValidationService;
        this.roleService = roleService;
        this.membershipService = membershipService;
        this.visitService = visitService;
        this.userMapper = userMapper;
    }


    @Transactional
    @Override
    public UserResponse create(CreateUser request) {
        Tenant tenant = userService.getCurrentUser().getTenant();

        User member = userMapper.toMember(tenant, request);

        userValidationService.validateGlobalAndTenantScopedCredentials(request.getEmail(), request.getPhone(), tenant.getId());

        userPasswordService.setupMemberInitialPassword(member);
        Membership membership = new Membership()
                .setTenant(tenant)
                .setUser(member)
                .setSubscriptionStatus(SubscriptionStatus.INACTIVE)
                .setAllowedVisits(0)
                .setRemainingVisits(0);

        member.getMemberships().add(membership);

        userService.save(member);
        membershipService.save(membership);

        LOGGER.info("Successfully added member with ID {} to facility '{}'", member.getId(), tenant.getName());
        return  userMapper.toResponse(membership, member);
    }

    @Override
    @Transactional
    public void deleteMember(UUID memberId) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        User user = userService.getByIdAndTenantId(memberId, tenant.getId());
        userService.delete(user);
        LOGGER.info("Member with ID {} deleted successfully", memberId);
    }

    @Transactional
    @Override
    public UserResponse checkInMember(UUID memberId) {
        User user = userService.findUserById(memberId);
        Membership activeMembership = membershipService.getRequiredActiveMembership(user.getMemberships());

        Membership updatedMembership = membershipService.checkIn(activeMembership);

        if (updatedMembership.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
           visitService.checkIn(updatedMembership, memberId);
        }

        return userMapper.toResponse(updatedMembership, user);
    }

    @Override
    @Transactional
    public UserResponse updateMember(UUID memberId, UserUpdate updateRequest) {

        User member = userService.findUserById(memberId);

        Membership membership = member.getCurrentMembership();
        userMapper.updateUserFields(updateRequest, member);
        userMapper.updateMembershipFields(updateRequest, membership);

        return userMapper.toResponse(membership, member);
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
                .map(this::mapToTableResponse).toList();
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
                .map(this::mapToTableResponse)
                .toList();
    }

    @Transactional
    @Override
    public List<UserResponse> findMember(MemberFilter filter) {
        List<User> users = findFirstMemberByFilter(filter);

        return users.stream().map(user -> {
            Membership membership = user.getMemberships().stream()
                    .max(Comparator.comparing(Membership::getCreatedAt))
                    .orElseThrow(() -> new IllegalStateException("User has no memberships"));

           return userMapper.toResponse(membership, user);
        }).toList();
    }

    private List<User> findFirstMemberByFilter(MemberFilter filter) {
        Tenant tenant = userService.getCurrentUser().getTenant();

        LOGGER.warn("Searching users with filter: {}", filter);
        LOGGER.warn("Tenant ID: {}", tenant.getId());

        Specification<User> spec = MemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenant.getId()));

        return userService.findFirstMemberByFilter(spec);
    }

    private MemberTableDto mapToTableResponse(User user) {
        Membership membership = user.getCurrentMembership();

        return MemberTableDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .gender(user.getGender())
                .roles(UserRoleHelper.extractRoleTypes(user))
                .birthDate(user.getBirthDate())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .employment(membership.getEmployment())
                .subscriptionPlan(membership.getSubscriptionPlan())
                .subscriptionStatus(membership.getSubscriptionStatus())
                .allowedVisits(membership.getAllowedVisits())
                .remainingVisits(membership.getRemainingVisits())
                .build();
    }
}
