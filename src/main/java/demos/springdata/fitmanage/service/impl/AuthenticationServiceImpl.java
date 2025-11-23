package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.client.PaymentFeignClient;
import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.RegisterResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponse;
import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantRegisterRequest;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.SecurityCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final PaymentFeignClient paymentFeignClient;

    @Autowired
    public AuthenticationServiceImpl
            (
                    AuthenticationManager authenticationManager,
                    UserRepository userRepository,
                    TenantRepository tenantRepository,
                    CustomUserDetailsService customUserDetailsService,
                    EmailService emailService,
                    BCryptPasswordEncoder passwordEncoder,
                    UserMapper userMapper, PaymentFeignClient paymentFeignClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.customUserDetailsService = customUserDetailsService;
        this.tenantRepository = tenantRepository;
        this.userMapper = userMapper;
        this.paymentFeignClient = paymentFeignClient;
    }

    @Transactional
    @Override
    public RegisterResponse registerUser(UserRegisterRequest request, TenantRegisterRequest tenantRequest) {
        LOGGER.info("Registration attempt for email: {}", request.getEmail());
        validateCredentials(request);

        Tenant tenant = Tenant.builder()
                .name(tenantRequest.getName())
                .businessEmail(tenantRequest.getBusinessEmail())
                .address(tenantRequest.getAddress())
                .city(tenantRequest.getCity())
                .build();

        tenantRepository.save(tenant);

        User user = userMapper.toAdminUser(tenant, request);

        //createTenantStripeAccount(tenant);
        userRepository.save(user);

        emailService.sendVerificationEmail(user);
        LOGGER.info("Registration successful for user: {}", user.getEmail());

        return new RegisterResponse(user.getEmail(), user.getVerificationCode());
    }

    @Override
    public EmailResponse findUserEmail(EmailValidationRequest emailValidationRequest) {
        return this.userRepository.findByEmail(emailValidationRequest.email())
                .map(user -> new EmailResponse(user.getEmail()))
                .orElseThrow(() -> {
                    LOGGER.warn("Account with email: {} does not exists", emailValidationRequest.email());
                    return new DamilSoftException(String.format("Account with email: %s does not exists.", emailValidationRequest.email()), HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public UserDetails authenticateUser(LoginRequest loginRequest) {
        LOGGER.info("Login attempt for email: {}", loginRequest.getEmail());
        UserDetails authUser = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        verifyAccountStatus(authUser);
        authenticateCredentials(loginRequest);
        LOGGER.info("Login successful for user: {}", loginRequest.getEmail());
        return authUser;
    }

    @Override
    public VerificationResponse verifyUserRegistration(VerificationRequest verificationRequest) {
        LOGGER.info("Verification attempt for user: {}", verificationRequest.email());

        User user = getUserByEmailOrElseThrow(verificationRequest.email());
        validateVerificationCode(user, verificationRequest.verificationCode());
        enableUserAccount(user);

        LOGGER.info("User successfully verified: {}", user.getEmail());

        return new VerificationResponse("Account verified successfully", true);
    }

    @Override
    public VerificationResponse resendUserVerificationCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new DamilSoftException("User not found!", HttpStatus.NOT_FOUND));

        if (user.isEnabled()) {
            throw new DamilSoftException("Account is already verified", HttpStatus.OK);
        }
        LOGGER.info("Resending verification code to: {}", email);

        user.setVerificationCode(SecurityCodeGenerator.generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
        emailService.sendVerificationEmail(user);
        userRepository.save(user);


        return new VerificationResponse("New verification code successfully delivered", true);
    }

    @Override
    public String changePassword(UserData userData, ChangePasswordRequest request) {
        User user = userRepository.findById(userData.getId()).orElseThrow(() -> new DamilSoftException("User not found!", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new DamilSoftException("Old password is incorrect", HttpStatus.UNAUTHORIZED);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new DamilSoftException("New password cannot be the same as old password", HttpStatus.CONFLICT);
        }

        if (!passwordEncoder.matches(request.getNewPassword(), request.getConfirmPassword())) {
            throw new DamilSoftException("New password and confirm password do not match!", HttpStatus.CONFLICT);
        }

        encryptUserPassword(user, request.getNewPassword());
        userRepository.save(user);

        LOGGER.debug("New Password {}", request.getNewPassword());
        return "Password changed successfully";
    }

    private void enableUserAccount(User user) {
        LOGGER.info("Enabling account for user: {}", user.getEmail());
        user.setEnabled(true)
                .setVerificationCode(null)
                .setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    private void validateVerificationCode(User user, String code) {
        Map<String, String> errors = new HashMap<>();
        if (user.getVerificationCodeExpiresAt() == null) {
            LOGGER.warn("Account with email {} is already verified", user.getUsername());
            throw new DamilSoftException("Account is already verified", HttpStatus.CONFLICT);
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
                    return new DamilSoftException("User not found", HttpStatus.NOT_FOUND);
                });
    }

    private void authenticateCredentials(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            LOGGER.warn("Login failed for email: {}", loginRequest.getEmail());
            throw new DamilSoftException("Invalid password", HttpStatus.UNAUTHORIZED);
        }
    }

    private static void verifyAccountStatus(UserDetails authUser) {
        if (authUser instanceof UserData user) {
            if (!user.isEnabled()) {
                throw new DamilSoftException("Account not verified. Please verify your account!", HttpStatus.UNAUTHORIZED);
            }
        }
    }

    private void validateCredentials(UserRegisterRequest request) {
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

    private void encryptUserPassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
    }

    private void createTenantStripeAccount(Tenant tenant) {

        TenantDto tenantDto = TenantDto.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .businessEmail(tenant.getBusinessEmail())
                .address(tenant.getAddress())
                .city(tenant.getCity())
                .build();


        try {
            paymentFeignClient.createConnectedAccount(tenantDto);
            tenant.setStripeAccountId(tenant.getStripeAccountId());
        } catch (Exception e) {
            throw new DamilSoftException("Unsuccessful creation of stripe account via Microservice: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
