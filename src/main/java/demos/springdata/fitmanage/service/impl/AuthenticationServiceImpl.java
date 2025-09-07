package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.security.CustomUserDetails;
import demos.springdata.fitmanage.service.AuthenticationService;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import demos.springdata.fitmanage.service.EmailService;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CurrentUserUtils currentUserUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);


    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, RoleService roleService, AuthenticationManager authenticationManager, EmailService emailService, CustomUserDetailsService customUserDetailsService, TenantRepository tenantRepository, CurrentUserUtils currentUserUtils) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.customUserDetailsService = customUserDetailsService;
        this.tenantRepository = tenantRepository;
        this.currentUserUtils = currentUserUtils;
    }

    @Transactional
    @Override
    public RegistrationResponseDto registerUser(RegistrationRequestDto registrationRequest, TenantDto tenantDto) {
        LOGGER.info("Registration attempt for email: {}", registrationRequest.getEmail());
        validateCredentials(registrationRequest);

        Tenant tenant = new Tenant();
        modelMapper.map(tenantDto, tenant);

        tenantRepository.save(tenant);

        User user = initializeNewUser(registrationRequest);
        user.setTenant(tenant);

        userRepository.save(user);
        LOGGER.info("Registration successful for user: {}", user.getEmail());

        return new RegistrationResponseDto(
                user.getEmail(),
                user.getVerificationCode()
        );
    }

    @Override
    public EmailResponseDto checkIfEmailIsAvailable(UserEmailRequestDto userEmailRequestDto) {
        return this.userRepository.findByEmail(userEmailRequestDto.getEmail())
                .map(user -> modelMapper.map(user, EmailResponseDto.class))
                .orElseThrow(() -> {
                    LOGGER.warn("Account with email: {} does not exists", userEmailRequestDto.getEmail());
                    return new FitManageAppException(String.format("Account with email: %s does not exists.", userEmailRequestDto.getEmail()), ApiErrorCode.NOT_FOUND);
                });
    }

    @Override
    public UserDetails authenticateUser(LoginRequestDto loginRequestDto) {
        LOGGER.info("Login attempt for email: {}", loginRequestDto.getEmail());
        UserDetails authUser = customUserDetailsService.loadUserByUsername(loginRequestDto.getEmail());
        verifyAccountStatus(authUser);
        authenticateCredentials(loginRequestDto);
        LOGGER.info("Login successful for user: {}", loginRequestDto.getEmail());
        return authUser;
    }

    @Override
    public VerificationResponseDto verifyUserRegistration(VerificationRequestDto verificationRequestDto) {
        LOGGER.info("Verification attempt for user: {}", verificationRequestDto.getEmail());

        User user = getUserByEmailOrElseThrow(verificationRequestDto.getEmail());
        validateVerificationCode(user, verificationRequestDto.getVerificationCode());
        enableUserAccount(user);

        LOGGER.info("User successfully verified: {}", user.getEmail());

        return new VerificationResponseDto("Account verified successfully", true);
    }

    @Override
    public VerificationResponseDto resendUserVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));

        if (user.isEnabled()) {
            throw new FitManageAppException("Account is already verified", ApiErrorCode.OK);
        }
        LOGGER.info("Resending verification code to: {}", email);

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
        sendVerificationEmail(user);
        userRepository.save(user);


        return new VerificationResponseDto("New verification code successfully delivered", true);
    }

    @Override
    public VerificationResponseDto changePassword(ChangePasswordRequest request) {
       User user = currentUserUtils.getCurrentUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return new VerificationResponseDto("Old password is incorrect", false);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return new VerificationResponseDto("New password cannot be the same as old password", false);
        }

        encryptUserPassword(user, request.getNewPassword());
        userRepository.save(user);

        LOGGER.debug("New Password {}", request.getNewPassword());
        return new VerificationResponseDto("Password changed successfully", true);
    }

    private void enableUserAccount(User user) {
        LOGGER.info("Enabling account for user: {}", user.getEmail());
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    private void validateVerificationCode(User user, String code) {
        Map<String, String> errors = new HashMap<>();
        if (user.getVerificationCodeExpiresAt() == null) {
            LOGGER.warn("Account with email {} is already verified", user.getUsername());
            throw new FitManageAppException("Account is already verified", ApiErrorCode.CONFLICT);
        }

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            LOGGER.warn("VerificationCode expired for user {}", user.getUsername());
            errors.put("verificationCode", "Verification code expired");
        }

        if (!user.getVerificationCode().equals(code)) {
            LOGGER.warn("Invalid verification code for user {}", user.getEmail());
            errors.put("verificationCode", "Invalid verification code");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }


    }

    private User getUserByEmailOrElseThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.error("Verification failed: User not found");
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }

    private void authenticateCredentials(LoginRequestDto loginRequestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            LOGGER.warn("Login failed for email: {}", loginRequestDto.getEmail());
            throw new FitManageAppException("Invalid email or password", ApiErrorCode.UNAUTHORIZED);
        }
    }

    private static void verifyAccountStatus(UserDetails authUser) {
        if (authUser instanceof CustomUserDetails user) {
            if (!user.isEnabled()) {
                throw new FitManageAppException("Account not verified. Please verify your account"
                        , ApiErrorCode.UNAUTHORIZED);
            }
        }
    }

    private User initializeNewUser(RegistrationRequestDto requestDto) {
        User user = mapUser(requestDto)
                .setGender(Gender.NOT_SPECIFIED)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        encryptUserPassword(user, requestDto.getPassword());

        Role gymAdminRole = roleService.findByName(RoleType.FACILITY_ADMIN);
        user.getRoles().add(gymAdminRole);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        sendVerificationEmail(user);
        return user;
    }

    //TODO: duplicate method
    private void validateCredentials(RegistrationRequestDto request) {
        Map<String, String> errors = new HashMap<>();
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            LOGGER.warn("User with email {} already exists", request.getEmail());
            errors.put("email", "Email is already registered");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            LOGGER.warn("Passwords do not match.");
            errors.put("confirmPassword", "Passwords do not match");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }

    //TODO: htmlMessage code extract, Update with company logo
    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            LOGGER.info("Sending verification email to: {}", user.getEmail());
            emailService.sendUserVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send verification email to: {}", user.getEmail(), e);
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void encryptUserPassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
    }

    private User mapUser(RegistrationRequestDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
