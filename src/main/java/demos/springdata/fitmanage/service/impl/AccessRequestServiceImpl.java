package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponse;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import org.springframework.http.HttpStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;


@Service
public class AccessRequestServiceImpl implements AccessRequestService {
    private final TenantService tenantService;
    private final MembershipService membershipService;
    private final RoleService roleService;
    private final UserService userService;
    private final UserValidationService userValidationService;
    private final UserPasswordService userPasswordService;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessRequestServiceImpl.class);

    @Autowired
    public AccessRequestServiceImpl
            (
                    TenantService tenantService,
                    MembershipService membershipService,
                    RoleService roleService,
                    UserService userService,
                    UserValidationService userValidationService,
                    UserPasswordService userPasswordService,
                    ModelMapper modelMapper
            )
    {
        this.tenantService = tenantService;
        this.membershipService = membershipService;
        this.roleService = roleService;
        this.userService = userService;
        this.userValidationService = userValidationService;
        this.userPasswordService = userPasswordService;
        this.modelMapper = modelMapper;
    }


    @Transactional
    @Override
    public MemberResponse requestAccess(Long tenantId, CreateUser request) {
        Tenant tenant = tenantService.getTenantById(tenantId);

        User member = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .phone(request.getPhone())
                .tenant(tenant)
                .enabled(false)
                .roles(Set.of(roleService.findByName(RoleType.MEMBER)))
                .build();

        Membership membership = new Membership()
                .setTenant(tenant)
                .setUser(member)
                .setSubscriptionStatus(SubscriptionStatus.PENDING)
                .setAllowedVisits(0)
                .setRemainingVisits(0);
        member.getMemberships().add(membership);

        userValidationService.validateTenantScopedCredentials(request.getEmail(), request.getPhone(), tenantId);
        userPasswordService.setupMemberInitialPassword(member);
        userService.save(member);
        membershipService.save(membership);

        LOGGER.info("Access request created for user {} in facility {}", member.getEmail(), tenant.getName());
        return MemberResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .username(member.getUsername())
                .email(member.getEmail())
                .gender(member.getGender())
                .roles(Set.of(RoleType.MEMBER))
                .birthDate(member.getBirthDate())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .phone(member.getPhone())
                .address(member.getAddress())
                .city(member.getCity())
                .subscriptionPlan(membership.getSubscriptionPlan())
                .subscriptionStatus(membership.getSubscriptionStatus())
                .subscriptionStartDate(membership.getSubscriptionStartDate())
                .subscriptionEndDate(membership.getSubscriptionEndDate())
                .allowedVisits(membership.getAllowedVisits())
                .remainingVisits(membership.getRemainingVisits())
                .lastCheckInAt(membership.getLastCheckInAt())
                .employment(membership.getEmployment())
                .build();
    }

    @Transactional
    @Override
    public MemberResponse processAccessRequest(Long userId, boolean approve) {
        User member = userService.findUserById(userId);

        Membership membership = member.getMemberships().stream().findFirst().get();

        if (membership.getSubscriptionStatus() != SubscriptionStatus.PENDING) {
            throw new DamilSoftException("Membership request already processed.", HttpStatus.BAD_REQUEST);
        }

        if (approve) {
            member.setEnabled(true);
            membership.setSubscriptionStatus(SubscriptionStatus.INACTIVE);

            userService.save(member);
            membershipService.save(membership);

            //todo: send email to the user that he is approved and he can use his password to log in
            //sendApprovalEmail(member, membership);

            LOGGER.info("Access request approved for user {}", member.getEmail());

        } else {

            membership.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
            membershipService.save(membership);

            //todo: send email to the user that his request was rejected
            //sendRejectionEmail(member);

            LOGGER.warn("Access request rejected for user {}", member.getEmail());
        }

        return MemberResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .username(member.getUsername())
                .email(member.getEmail())
                .gender(member.getGender())
                .roles(Set.of(RoleType.MEMBER))
                .birthDate(member.getBirthDate())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .phone(member.getPhone())
                .address(member.getAddress())
                .city(member.getCity())
                .subscriptionPlan(membership.getSubscriptionPlan())
                .subscriptionStatus(membership.getSubscriptionStatus())
                .subscriptionStartDate(membership.getSubscriptionStartDate())
                .subscriptionEndDate(membership.getSubscriptionEndDate())
                .allowedVisits(membership.getAllowedVisits())
                .remainingVisits(membership.getRemainingVisits())
                .lastCheckInAt(membership.getLastCheckInAt())
                .employment(membership.getEmployment())
                .build();
    }
}
