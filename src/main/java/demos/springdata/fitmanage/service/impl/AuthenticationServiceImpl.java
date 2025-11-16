package demos.springdata.fitmanage.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.RegisterResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponse;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.SecurityCodeGenerator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final StripeConnectService stripeConnectService;
    private final CustomUserDetailsService customUserDetailsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final UserService userService;



    @Value("${stripe.api.key}")
    private String apiKey;


    @Autowired
    public AuthenticationServiceImpl
            (
            UserRepository userRepository,
            ModelMapper modelMapper,
            BCryptPasswordEncoder passwordEncoder,
            RoleService roleService,
            AuthenticationManager authenticationManager,
            EmailService emailService,
            CustomUserDetailsService customUserDetailsService,
            TenantRepository tenantRepository,
            StripeConnectService stripeConnectService,
            UserService userService
            ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.customUserDetailsService = customUserDetailsService;
        this.tenantRepository = tenantRepository;
        this.stripeConnectService = stripeConnectService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public RegisterResponse registerUser(RegisterRequest registrationRequest, TenantDto tenantDto) {
        Stripe.apiKey = apiKey;
        LOGGER.info("Registration attempt for email: {}", registrationRequest.getEmail());
        validateCredentials(registrationRequest);

        Tenant tenant = new Tenant();
        modelMapper.map(tenantDto, tenant);

        tenantRepository.save(tenant);

        User user = initializeNewUser(registrationRequest);
        user.setTenant(tenant);

        createTenantStripeAccount(user, tenant);

        userRepository.save(user);
        LOGGER.info("Registration successful for user: {}", user.getEmail());

        return new RegisterResponse(
                user.getEmail(),
                user.getVerificationCode()
        );
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
        User user = userService.findByEmail(email);

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
    public String changePassword(ChangePasswordRequest request) {
       User user = userService.getCurrentUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw  new DamilSoftException("Old password is incorrect", HttpStatus.UNAUTHORIZED);
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
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
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
                throw new DamilSoftException("Account not verified. Please verify your account"
                        , HttpStatus.UNAUTHORIZED);
            }
        }
    }

    private User initializeNewUser(RegisterRequest requestDto) {
        User user = mapUser(requestDto)
                .setGender(Gender.NOT_SPECIFIED)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        encryptUserPassword(user, requestDto.getPassword());

        Role gymAdminRole = roleService.findByName(RoleType.ADMIN);
        user.getRoles().add(gymAdminRole);
        user.setVerificationCode(SecurityCodeGenerator.generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

        emailService.sendVerificationEmail(user);

        return user;
    }

    private void validateCredentials(RegisterRequest request) {
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

    private User mapUser(RegisterRequest dto) {
        return modelMapper.map(dto, User.class);
    }

    private void createTenantStripeAccount(User user, Tenant tenant) {
        try {
            Account stripeAccount = stripeConnectService.createConnectedAccount(user.getTenant());
            tenant.setStripeAccountId(stripeAccount.getId());
        } catch (StripeException e) {
            throw new DamilSoftException("Unsuccessful creation of stripe account: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
