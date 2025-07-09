package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.auth.request.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.RegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.VerificationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.response.GymEmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponseDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymRepository;
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
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserValidationService userValidationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);


    @Autowired
    public AuthenticationServiceImpl(GymRepository gymRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, RoleService roleService, AuthenticationManager authenticationManager, EmailService emailService, CustomUserDetailsService customUserDetailsService, UserValidationService userValidationService) {
        this.gymRepository = gymRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.customUserDetailsService = customUserDetailsService;
        this.userValidationService = userValidationService;
    }

    @Override
    public RegistrationResponseDto registerGym(RegistrationRequestDto gymRegistrationDto) {
        LOGGER.info("Registration attempt for email: {}", gymRegistrationDto.getEmail());

        Map<String, String> errors = new HashMap<>();
        userValidationService.checkDuplicateEmailOrThrow(gymRegistrationDto.getEmail());
        validateCredentials(gymRegistrationDto, errors);
        Gym gym = initializeNewGym(gymRegistrationDto);

        gymRepository.save(gym);
        LOGGER.info("Registration successful for user: {}", gym.getEmail());

        return new RegistrationResponseDto(
                gym.getActualUsername(),
                gym.getEmail(),
                gym.getVerificationCode()
        );
    }


    @Override
    public Optional<GymEmailResponseDto> validateEmail(GymEmailRequestDto gymEmailRequestDto) {
        Map<String, String> errors = new HashMap<>();
        Optional<Gym> gym = this.gymRepository.findByEmail(gymEmailRequestDto.getEmail());
        if (gym.isPresent()) {
            return gym.map(g -> modelMapper.map(g, GymEmailResponseDto.class));
        } else {
            LOGGER.warn("Account with email: {} does not exists", gymEmailRequestDto.getEmail());
            errors.put("account", "Account with this email does not exist");
            throw new MultipleValidationException(errors);
        }
    }

    @Override
    public UserDetails login(LoginRequestDto loginRequestDto) {
        LOGGER.info("Login attempt for email: {}", loginRequestDto.getEmail());
        UserDetails authUser = customUserDetailsService.loadUserByUsername(loginRequestDto.getEmail());
        verifyAccountStatus(authUser);
        authenticateCredentials(loginRequestDto);
        LOGGER.info("Login successful for user: {}", loginRequestDto.getEmail());
        return authUser;
    }


    @Override
    public VerificationResponseDto verifyUser(VerificationRequestDto verificationRequestDto) {
        Map<String, String> errors = new HashMap<>();
        LOGGER.info("Verification attempt for user: {}", verificationRequestDto.getEmail());

        Gym gym = getGymByEmailOrElseThrow(verificationRequestDto.getEmail());

        validateVerificationCode(gym, verificationRequestDto.getVerificationCode(), errors);

        enableGymAccount(gym);

        LOGGER.info("User successfully verified: {}", gym.getEmail());
        return new VerificationResponseDto("Account verified successfully", true);
    }




    @Override
    public VerificationResponseDto resendVerificationCode(String email) {

        Optional<Gym> optionalUser = gymRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Gym gym = optionalUser.get();

            if (gym.isEnabled()) {
                throw new FitManageAppException("Account is already verified", ApiErrorCode.OK);
            }

            LOGGER.info("Resending verification code to: {}", email);

            gym.setVerificationCode(generateVerificationCode());
            gym.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(gym);
            gymRepository.save(gym);
        } else {
            throw new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
        }

        return new VerificationResponseDto("New verification code successfully delivered", true);
    }

    private void enableGymAccount(Gym gym) {
        LOGGER.info("Enabling account for user: {}", gym.getEmail());
        gym.setEnabled(true);
        gym.setVerificationCode(null);
        gym.setVerificationCodeExpiresAt(null);
        gymRepository.save(gym);
    }

    private void validateVerificationCode(Gym gym, String code, Map<String, String> errors) {
        if (gym.getVerificationCodeExpiresAt() == null) {
            LOGGER.warn("Account {} is already verified", gym.getActualUsername());
            errors.put("account", "Account is already verified");
            throw new MultipleValidationException(errors);
        }

        if (gym.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            LOGGER.warn("Verification failed: verificationCode expired for user {}", gym.getEmail());
            errors.put("verificationCode", "Verification code expired");
        }

        if (!gym.getVerificationCode().equals(code)) {
            LOGGER.warn("Verification failed: Invalid code for user {}", gym.getEmail());
            errors.put("verificationCode", "Invalid verification code");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }


    }

    private Gym getGymByEmailOrElseThrow(String email) {
        return gymRepository.findByEmail(email)
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
        if (authUser instanceof Gym gym) {
            if (!gym.isEnabled()) {
                throw new FitManageAppException("Account not verified. Please verify your account", ApiErrorCode.UNAUTHORIZED);
            }
        }
    }

    private Gym initializeNewGym(RegistrationRequestDto gymRegistrationDto) {
        Gym gym = mapGym(gymRegistrationDto);
        encryptGymPassword(gym);
        gym.setCreatedAt(LocalDateTime.now());

        Role gymAdminRole = roleService.findByName(RoleType.GYM_ADMIN);
        gym.getRoles().add(gymAdminRole);

        gym.setVerificationCode(generateVerificationCode());
        gym.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        gym.setEnabled(false);
        sendVerificationEmail(gym);
        return gym;
    }

    private void validateCredentials(RegistrationRequestDto gymRegistrationDto, Map<String, String> errors) {
        if (gymRepository.findByUsername(gymRegistrationDto.getUsername()).isPresent()) {
            LOGGER.warn("Username {} already exists", gymRegistrationDto.getUsername());
            errors.put("username", "Gym with this username already exists");
        }

        if (gymRepository.findByEmail(gymRegistrationDto.getEmail()).isPresent()) {
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

    private void sendVerificationEmail(Gym gym) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + gym.getVerificationCode();
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
            LOGGER.info("Sending verification email to: {}", gym.getEmail());
            emailService.sendVerificationEmail(gym.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send verification email to: {}", gym.getEmail(), e);
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void encryptGymPassword(Gym gym) {
        String encryptedPassword = passwordEncoder.encode(gym.getPassword());
        gym.setPassword(encryptedPassword);
    }

    private <T> Gym mapGym(T dto) {
        return modelMapper.map(dto, Gym.class);
    }
}
