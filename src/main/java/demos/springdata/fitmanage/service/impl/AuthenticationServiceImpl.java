package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.auth.request.UserEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.RegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.VerificationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.response.GymEmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.service.AuthenticationService;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import demos.springdata.fitmanage.service.EmailService;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.validation.UserValidationService;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CustomUserDetailsService customUserDetailsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);


    @Autowired
    public AuthenticationServiceImpl(GymRepository gymRepository, UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, RoleService roleService, AuthenticationManager authenticationManager, EmailService emailService, CustomUserDetailsService customUserDetailsService, UserValidationService userValidationService, TenantRepository tenantRepository) {
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.customUserDetailsService = customUserDetailsService;

        this.tenantRepository = tenantRepository;
    }

    @Override
    public RegistrationResponseDto registerGym(RegistrationRequestDto gymRegistrationDto, TenantDto tenantDto) {
        LOGGER.info("Registration attempt for email: {}", gymRegistrationDto.getEmail());
        Map<String, String> errors = new HashMap<>();
        validateCredentials(gymRegistrationDto, errors);

        Tenant tenant = new Tenant()
                .setName(tenantDto.getName()).setSubscriptionValidUntil(null);

        tenantRepository.save(tenant);

        User user = initializeNewUser(gymRegistrationDto);

        userRepository.save(user);
        LOGGER.info("Registration successful for user: {}", user.getEmail());

        return new RegistrationResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.getVerificationCode()
        );
    }


    @Override
    public Optional<GymEmailResponseDto> checkIfEmailIsAvailable(UserEmailRequestDto userEmailRequestDto) {
        Map<String, String> errors = new HashMap<>();
        Optional<User> user = this.userRepository.findByEmail(userEmailRequestDto.getEmail());
        if (user.isPresent()) {
            return user.map(g -> modelMapper.map(g, GymEmailResponseDto.class));
        } else {
            LOGGER.warn("Account with email: {} does not exists", userEmailRequestDto.getEmail());
            errors.put("account", "Account with this email does not exist");
            throw new MultipleValidationException(errors);
        }
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
        Map<String, String> errors = new HashMap<>();
        LOGGER.info("Verification attempt for user: {}", verificationRequestDto.getEmail());

        User user = getUserByEmailOrElseThrow(verificationRequestDto.getEmail());

        validateVerificationCode(user, verificationRequestDto.getVerificationCode(), errors);


        enableUserAccount(user);

        LOGGER.info("User successfully verified: {}", user.getEmail());
        return new VerificationResponseDto("Account verified successfully", true);
    }




    @Override
    public VerificationResponseDto resendUserVerificationCode(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.isEnabled()) {
                throw new FitManageAppException("Account is already verified", ApiErrorCode.OK);
            }

            LOGGER.info("Resending verification code to: {}", email);

            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
        }

        return new VerificationResponseDto("New verification code successfully delivered", true);
    }

    private void enableUserAccount(User user) {
        LOGGER.info("Enabling account for user: {}", user.getEmail());
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    private void validateVerificationCode(User user, String code, Map<String, String> errors) {
        if (user.getVerificationCodeExpiresAt() == null) {
            LOGGER.warn("Account {} is already verified", user.getUsername());
            errors.put("account", "Account is already verified");
            throw new MultipleValidationException(errors);
        }

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            LOGGER.warn("Verification failed: verificationCode expired for user {}", user.getEmail());
            errors.put("verificationCode", "Verification code expired");
        }

        if (!user.getVerificationCode().equals(code)) {
            LOGGER.warn("Verification failed: Invalid code for user {}", user.getEmail());
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
        if (authUser instanceof User user) {
            if (!user.isEnabled()) {
                throw new FitManageAppException("Account not verified. Please verify your account", ApiErrorCode.UNAUTHORIZED);
            }
        }
    }

    private User initializeNewUser(RegistrationRequestDto requestDto) {
        User user = mapUser(requestDto);
        encryptUserPassword(user);
        user.setCreatedAt(LocalDateTime.now());

        Role gymAdminRole = roleService.findByName(RoleType.GYM_ADMIN);
        user.getRoles().add(gymAdminRole);

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return user;
    }

    private void validateCredentials(RegistrationRequestDto gymRegistrationDto, Map<String, String> errors) {
        if (userRepository.findByUsername(gymRegistrationDto.getUsername()).isPresent()) {
            LOGGER.warn("Username {} already exists", gymRegistrationDto.getUsername());
            errors.put("username", "Gym with this username already exists");
        }

        if (userRepository.findByEmail(gymRegistrationDto.getEmail()).isPresent()) {
            LOGGER.warn("Account with email {} already exists", gymRegistrationDto.getEmail());
            errors.put("email", "Email is already registered");
        }

        if (!gymRegistrationDto.getPassword().equals(gymRegistrationDto.getConfirmPassword())) {
            LOGGER.warn("Passwords do not match.");
            errors.put("confirmPassword", "Passwords do not match");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }

    private void sendVerificationEmail(User user) { //TODO: Update with company logo
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
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void encryptUserPassword(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
    }

    private <T> User mapUser(T dto) {
        return modelMapper.map(dto, User.class);
    }
}
