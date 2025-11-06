package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.member.request.MemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccessRequestServiceImpl implements AccessRequestService {
    private final TenantService tenantService;
    private final MembershipService membershipService;
    private final RoleService roleService;
    private final UserService userService;
    private final UserPasswordService userPasswordService;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessRequestServiceImpl.class);

    @Autowired
    public AccessRequestServiceImpl(TenantService tenantService, MembershipService membershipService, RoleService roleService, UserService userService, UserPasswordService userPasswordService, EmailService emailService, BCryptPasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.tenantService = tenantService;
        this.membershipService = membershipService;
        this.roleService = roleService;
        this.userService = userService;
        this.userPasswordService = userPasswordService;
        this.modelMapper = modelMapper;
    }


    @Transactional
    @Override
    public MemberResponseDto requestAccess(Long tenantId, MemberCreateRequestDto memberCreateRequestDto) {

        Tenant tenant = tenantService.getTenantById(tenantId);

        User member = new User()
                .setFirstName(memberCreateRequestDto.getFirstName())
                .setLastName(memberCreateRequestDto.getLastName())
                .setGender(memberCreateRequestDto.getGender())
                .setBirthDate(memberCreateRequestDto.getBirthDate())
                .setEmail(memberCreateRequestDto.getEmail())
                .setPhone(memberCreateRequestDto.getPhone())
                .setTenant(tenant)
                .setEnabled(false);


        Role role = roleService.findByName(RoleType.MEMBER);
        member.getRoles().add(role);

        Membership membership = new Membership()
                .setTenant(tenant)
                .setUser(member)
                .setSubscriptionStatus(SubscriptionStatus.PENDING)
                .setAllowedVisits(0)
                .setRemainingVisits(0);
        member.getMemberships().add(membership);

        validateCredentials(member, memberCreateRequestDto);

        userPasswordService.setupMemberInitialPassword(member);
        userService.save(member);
        membershipService.save(membership);

        LOGGER.info("Access request created for user {} in facility {}", member.getEmail(), tenant.getName());
        MemberResponseDto responseDto = modelMapper.map(member, MemberResponseDto.class);
        responseDto.getRoles().add(RoleType.MEMBER);

        return responseDto;
    }

    @Transactional
    @Override
    public MemberResponseDto processAccessRequest(Long userId, boolean approve) {
        User member = userService.findUserById(userId);

        Membership membership = member.getMemberships().stream().findFirst().get();

        if (membership.getSubscriptionStatus() != SubscriptionStatus.PENDING) {
            throw new FitManageAppException("Membership request already processed.", ApiErrorCode.BAD_REQUEST);
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


        return modelMapper.map(member, MemberResponseDto.class).setRoles(UserRoleHelper.extractRoleTypes(member));
    }


    //TODO: logic is same in other classes
    private void validateCredentials(User member, MemberCreateRequestDto requestDto) {
        Map<String, String> errors = new HashMap<>();

        if (userService.existsByEmailAndTenant(requestDto.getEmail(), member.getTenant().getId())) {
            LOGGER.warn("User with email {} already exists", requestDto.getEmail());
            errors.put("email", "Email is already registered");
        }

        if (userService.existsByPhoneAndTenant(requestDto.getPhone(), member.getTenant().getId())) {
            LOGGER.warn("User with phone {} already exists", member.getPhone());
            errors.put("phone", "Phone used from another user");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }
}
