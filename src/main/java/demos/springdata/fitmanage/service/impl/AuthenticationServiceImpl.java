package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.auth.*;
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
import demos.springdata.fitmanage.util.ValidationUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
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
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final GymRepository gymRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationUtil validationUtil;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CustomUserDetailsService customUserDetailsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);


    @Autowired
    public AuthenticationServiceImpl(GymRepository gymRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, ValidationUtil validationUtil, RoleService roleService, AuthenticationManager authenticationManager, EmailService emailService, CustomUserDetailsService customUserDetailsService) {
        this.gymRepository = gymRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.validationUtil = validationUtil;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public RegistrationResponseDto registerGym(RegistrationRequestDto gymRegistrationDto) {
        Map<String, String> errors = new HashMap<>();
        validateDto(gymRegistrationDto);

        if (gymRepository.findByUsername(gymRegistrationDto.getUsername()).isPresent()) {
            LOGGER.error("Username {} already exists", gymRegistrationDto.getUsername());
            errors.put("username", "Gym with this username already exists");
        }

        if (gymRepository.findByEmail(gymRegistrationDto.getEmail()).isPresent()) {
            LOGGER.error("Account with email {} already exists", gymRegistrationDto.getEmail());
            errors.put("email", "Email is already registered");
        }

        if (!gymRegistrationDto.getPassword().equals(gymRegistrationDto.getConfirmPassword())) {
            LOGGER.error("Passwords do not match.");
            errors.put("confirmPassword", "Passwords do not match");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }

        Gym gym = mapGym(gymRegistrationDto);
        encryptGymPassword(gym);
        gym.setCreatedAt(LocalDateTime.now());

        Role gymAdminRole = roleService.findByName(RoleType.GYM_ADMIN);
        gym.getRoles().add(gymAdminRole);

        gym.setVerificationCode(generateVerificationCode());
        gym.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        gym.setEnabled(false);
        sendVerificationEmail(gym);

        gymRepository.save(gym);
        return new RegistrationResponseDto(
                gym.getActualUsername(),
                gym.getEmail(),
                "Registration successful. Please check your email for verification.",
                gym.getVerificationCode()
        );
    }

    @Override
    public Gym validateEmail(GymEmailRequestDto gymEmailRequestDto) {
        validateDto(gymEmailRequestDto);
        return this.gymRepository.findByEmail(gymEmailRequestDto.getEmail()).orElseThrow(() ->
                new FitManageAppException("Account with this email does not exist.", ApiErrorCode.CONFLICT));
    }

    @Override
    public UserDetails authenticate(LoginRequestDto loginRequestDto) {
        validateDto(loginRequestDto);

        UserDetails user = customUserDetailsService.loadUserByUsername(loginRequestDto.getEmail());

        if (user instanceof Gym gym) {
            if (!gym.isEnabled()) {
                throw new FitManageAppException("Account not verified. Please verify your account", ApiErrorCode.OK);
            }
        }

        try {
            LOGGER.info("Authentication attempt for user: {}", loginRequestDto.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            LOGGER.error("Authentication failed for user: {}", loginRequestDto.getEmail(), exception);
            throw exception;
        }


        LOGGER.info("User successfully authenticated: {}", user.getUsername());

        return user;
    }

    public VerificationResponseDto verifyUser(VerifyGymDto verifyGymDto) {
        LOGGER.info("Verification attempt for user: {}", verifyGymDto.getEmail());

        Optional<Gym> optionalGym = gymRepository.findByEmail(verifyGymDto.getEmail());
        if (optionalGym.isPresent()) {
            Gym gym = optionalGym.get();

            if (gym.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new FitManageAppException("Verification code expired.", ApiErrorCode.BAD_REQUEST);
            }

            if (gym.getVerificationCode().equals(verifyGymDto.getVerificationCode())) {
                gym.setEnabled(true);
                gym.setVerificationCode(null);
                gym.setVerificationCodeExpiresAt(null);
                gymRepository.save(gym);
                LOGGER.info("User successfully verified: {}", gym.getEmail());
                return new VerificationResponseDto("Account verified successfully", true);
            } else {
                LOGGER.error("Verification failed: Invalid verification code.");
                throw new FitManageAppException("Invalid verification code", ApiErrorCode.BAD_REQUEST);
            }
        } else {
            LOGGER.error("Verification failed: User not found");
            throw new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
        }
    }


    public void resendVerificationCode(String email) {
        Optional<Gym> optionalUser = gymRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Gym gym = optionalUser.get();

            if (gym.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }

            LOGGER.info("Resending verification code to: {}", email);

            gym.setVerificationCode(generateVerificationCode());
            gym.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(gym);
            gymRepository.save(gym);
        } else {
            throw new RuntimeException("User not found");
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


    private <T> void validateDto(T dto) {
        if (!validationUtil.isValid(dto)) {
            Set<ConstraintViolation<T>> violations = validationUtil.violations(dto);
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new FitManageAppException(errorMessage, ApiErrorCode.BAD_REQUEST);
        }
    }

    private void encryptGymPassword(Gym gym) {
        String encryptedPassword = passwordEncoder.encode(gym.getPassword());
        gym.setPassword(encryptedPassword);
    }

    private <T> Gym mapGym(T dto) {
        return modelMapper.map(dto, Gym.class);
    }
}
