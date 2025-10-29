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
import demos.springdata.fitmanage.util.SecurityCodeGenerator;
import demos.springdata.fitmanage.util.UserRoleHelper;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccessRequestServiceImpl implements AccessRequestService {
    private final TenantService tenantService;
    private final MembershipService membershipService;
    private final RoleService roleService;
    private final UserService userService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessRequestServiceImpl.class);

    @Autowired
    public AccessRequestServiceImpl(TenantService tenantService, MembershipService membershipService, RoleService roleService, UserService userService, EmailService emailService, BCryptPasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.tenantService = tenantService;
        this.membershipService = membershipService;
        this.roleService = roleService;
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
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
        createAndSendInitialPasswordToUser(member);

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
        //Membership membership = membershipService.getMembershipById(membershipId).orElseThrow(()-> new FitManageAppException("Membership not found", ApiErrorCode.NOT_FOUND));
        //User member = membership.getUser();

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

            // Изпращане на имейл за успешно одобрение
            //sendApprovalEmail(member, membership);

            LOGGER.info("Access request approved for user {}", member.getEmail());

        } else {

            membership.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
            membershipService.save(membership);

            //sendRejectionEmail(member);

            LOGGER.warn("Access request rejected for user {}", member.getEmail());
        }


        return modelMapper.map(member, MemberResponseDto.class).setRoles(UserRoleHelper.extractRoleTypes(member));
    }

    private void createAndSendInitialPasswordToUser(User member) {
        LOGGER.info("Initial password for user with email: {} will be created", member.getEmail());
        String initialPassword = SecurityCodeGenerator.generateDefaultPassword();
        sendInitialPassword(member, initialPassword);
        member.setPassword(passwordEncoder.encode(initialPassword))
                .setUpdatedAt(LocalDateTime.now());
    }

    //TODO: extract htmlMessage code, Update with company logo
    private void sendInitialPassword(User user, String initialPassword) {
        String subject = "Password";
        String password = "PASSWORD " + initialPassword;
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + password + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            LOGGER.info("Sending initial password to: {}", user.getEmail());
            emailService.sendUserVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send password to: {}", user.getEmail(), e);
            throw new FitManageAppException("Failed to send password to user", ApiErrorCode.INTERNAL_ERROR);
        }
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
