package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.authenticationDto.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.GymLoginRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.VerifyGymDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.AuthenticationService;
import demos.springdata.fitmanage.service.EmailService;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.util.ValidationUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
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


    @Autowired
    public AuthenticationServiceImpl(GymRepository gymRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, ValidationUtil validationUtil, RoleService roleService, AuthenticationManager authenticationManager, EmailService emailService) {
        this.gymRepository = gymRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.validationUtil = validationUtil;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    @Override
    public Gym registerGym(GymRegistrationRequestDto gymRegistrationDto) {

        validateDto(gymRegistrationDto);

        if (gymRepository.findByUsername(gymRegistrationDto.getUsername()).isPresent()) {
            throw new FitManageAppException("Gym with this name already exists", ApiErrorCode.CONFLICT);
        }

        if (gymRepository.findByEmail(gymRegistrationDto.getEmail()).isPresent()) {
            throw new FitManageAppException("Email is already registered", ApiErrorCode.CONFLICT);
        }

        if (!gymRegistrationDto.getPassword().equals(gymRegistrationDto.getConfirmPassword())) {
            throw new FitManageAppException("Passwords do not match", ApiErrorCode.BAD_REQUEST);
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

        return gym;
    }

    @Override
    public Gym validateEmail(GymEmailRequestDto gymEmailRequestDto) {
        validateDto(gymEmailRequestDto);
        return this.gymRepository.findByEmail(gymEmailRequestDto.getEmail()).orElseThrow(() ->
                new FitManageAppException("Account with this email does not exist.", ApiErrorCode.CONFLICT));
    }

    @Override
    public Gym authenticate(GymLoginRequestDto gymLoginRequestDto) {
        validateDto(gymLoginRequestDto);
        Gym gym = this.gymRepository.findByEmail(gymLoginRequestDto.getEmail()).orElseThrow(() ->
                new FitManageAppException("Account with this email does not exist.", ApiErrorCode.CONFLICT));

        if (!gym.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        gymLoginRequestDto.getEmail(),
                        gymLoginRequestDto.getPassword()
                )
        );

        return gym;
    }

    public void verifyUser(VerifyGymDto verifyGymDto) {
        Optional<Gym> optionalGym = gymRepository.findByEmail(verifyGymDto.getEmail());
        if (optionalGym.isPresent()) {
            Gym gym = optionalGym.get();
            if (gym.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (gym.getVerificationCode().equals(verifyGymDto.getVerificationCode())) {
                gym.setEnabled(true);
                gym.setVerificationCode(null);
                gym.setVerificationCodeExpiresAt(null);
                gymRepository.save(gym);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }


    public void resendVerificationCode(String email) {
        Optional<Gym> optionalUser = gymRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Gym gym = optionalUser.get();
            if (gym.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
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
            emailService.sendVerificationEmail(gym.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
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
