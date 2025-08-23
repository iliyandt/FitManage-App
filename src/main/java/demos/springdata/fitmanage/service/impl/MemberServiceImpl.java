package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.support.MemberSpecification;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserSecurityUtils;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {

    private final UserService userService;
    private final RoleService roleService;
    private final TenantService tenantService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserSecurityUtils securityUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    public MemberServiceImpl
            (UserService userService,
             RoleService roleService,
             TenantService tenantService,
             EmailService emailService,
             ModelMapper modelMapper,
             BCryptPasswordEncoder passwordEncoder, UserSecurityUtils securityUtils, MembershipService membershipService) {
        this.userService = userService;
        this.roleService = roleService;
        this.tenantService = tenantService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
    }

    @Transactional
    @Override
    public UserResponseDto createMember(UserCreateRequestDto requestDto) {
        String userEmail = getAuthenticatedUserEmail();
        Tenant tenant = getTenantByEmail(userEmail);

        User user;
        try {
            user = buildMember(tenant, requestDto);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send verification email while creating member: {}", requestDto.getEmail(), e);
            throw new FitManageAppException("Failed to send verification email", ApiErrorCode.INTERNAL_ERROR);
        }

        validateCredentials(user, requestDto);

        user.setTenant(tenant);
        userService.save(user);
        LOGGER.info("Successfully added member with ID {} to gym '{}'", user.getId(), tenant.getName());

        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public void removeMember(Long memberId) {
        String adminEmail = getAuthenticatedUserEmail();
        Tenant tenant = getTenantByEmail(adminEmail);

        User user = userService.getByIdAndTenantId(memberId, tenant.getId());

        LOGGER.info("Deleting member with ID {} from tenant {}", memberId, tenant.getName());
        userService.delete(user);
        LOGGER.info("Member with ID {} deleted successfully", memberId);
    }

    //TODO: add logic
    @Override
    public UserResponseDto checkInMember(Long memberId, String input) {
        User user = userService.findUserById(memberId);

        return null;
    }


    //TODO: add logic
    @Override
    public UserResponseDto updateMemberDetails(Long memberId, MemberUpdateDto updateRequest) {
        User user = userService.findUserById(memberId);
        return userService.updateProfile(user.getId(), updateRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberTableDto> getAllMembersForTable() {
        LOGGER.info("Fetching all members..");
        String email = getAuthenticatedUserEmail();
        Tenant tenant = getTenantByEmail(email);

        List<User> members = tenant.getUsers();

        return members.stream().filter(m -> m.getRoles().contains(roleService.findByName(RoleType.FACILITY_MEMBER)))
                .map(member -> modelMapper.map(member, MemberTableDto.class))
                .toList();
    }

    @Transactional
    @Override
    public List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto filter) {
        LOGGER.info("Search for users with filter: {}", filter);

        Tenant tenant = getTenantByEmail(getAuthenticatedUserEmail());

        Specification<User> spec = MemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("tenant"), tenant));

        List<User> memberList = userService.findMembersByFilter(spec);

        if (memberList.isEmpty())
            throw new FitManageAppException("No members found for the given filter", ApiErrorCode.NOT_FOUND);

        return memberList
                .stream()
                .map(member -> modelMapper.map(member, MemberTableDto.class))
                .toList();
    }

    @Override
    public UserResponseDto findMember(MemberFilterRequestDto filter) {
        return findFirstMemberByFilter(filter)
                .map(member -> modelMapper.map(member, UserResponseDto.class))
                .orElseThrow(() -> new FitManageAppException("Member not found", ApiErrorCode.NOT_FOUND));
    }

    private Tenant getTenantByEmail(String email) {
        return tenantService.getTenantByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("User with email {} not found.", email);
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        LOGGER.info("Authenticated user email: {}", email);
        return email;
    }

    //TODO: refactor for better separation of concerns
    private User buildMember(Tenant tenant, UserCreateRequestDto requestDto) throws MessagingException {
        User user = new User()
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setGender(requestDto.getGender())
                .setBirthDate(requestDto.getBirthDate())
                .setPhone(requestDto.getPhone())
                .setTenant(tenant)
                .setCreatedAt(LocalDateTime.now())
                .setVerificationCode(securityUtils.generateVerificationCode())
                .setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));


        LOGGER.info("Initial password for user with email: {} will be created", user.getEmail());
        String initialPassword = securityUtils.generateDefaultPassword();

        emailService.sendUserVerificationEmail(user.getEmail(), "Account verification", "Verification code: " + user.getVerificationCode());


        sendInitialPassword(user, initialPassword);

        user.setPassword(passwordEncoder.encode(initialPassword))
                .setUpdatedAt(LocalDateTime.now());

        Role role = roleService.findByName(RoleType.FACILITY_MEMBER);
        user.getRoles().add(role);

        return user;
    }

    private void validateCredentials(User member, UserCreateRequestDto requestDto) {
        Map<String, String> errors = new HashMap<>();

        if (userService.existsByEmailAndTenant(requestDto.getEmail(), member.getTenant().getId())) {
            LOGGER.warn("Member with email {} already exists", requestDto.getEmail());
            errors.put("email", "Email is already registered");
        }

        if (userService.existsByPhoneAndTenant(requestDto.getPhone(), member.getTenant().getId())) {
            LOGGER.warn("Member with phone {} already exists", member.getPhone());
            errors.put("phone", "Phone used from another member");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
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

    //TODO: what happens when searching by first and last name and there are 2 members with identical names?
    private Optional<User> findFirstMemberByFilter(MemberFilterRequestDto filter) {
        Tenant tenant = getTenantByEmail(getAuthenticatedUserEmail());

        Specification<User> spec = MemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("tenant"), tenant));

        return userService.findFirstMemberByFilter(spec);
    }
}
