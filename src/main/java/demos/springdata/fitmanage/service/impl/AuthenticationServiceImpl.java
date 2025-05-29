package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.GymLoginRequestDto;
import demos.springdata.fitmanage.domain.dto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.AuthenticationService;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.util.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final GymRepository gymRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationUtil validationUtil;
    private final RoleService roleService;

    @Autowired
    public AuthenticationServiceImpl(GymRepository gymRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, ValidationUtil validationUtil, RoleService roleService) {
        this.gymRepository = gymRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.validationUtil = validationUtil;
        this.roleService = roleService;
    }

    @Override
    public void registerGym(GymRegistrationRequestDto gymRegistrationDto) {

        validateDto(gymRegistrationDto);

        if (gymRepository.findByName(gymRegistrationDto.getName()).isPresent()) {
            throw new FitManageAppException("Gym with this name already exists", ApiErrorCode.CONFLICT);
        }

        if (gymRepository.findByEmail(gymRegistrationDto.getEmail()).isPresent()) {
            throw new FitManageAppException("Email is already registered", ApiErrorCode.CONFLICT);
        }

        Gym gym = mapGym(gymRegistrationDto);
        encryptGymPassword(gym);
        gym.setCreatedAt(LocalDateTime.now());

        // Assign GYM_ADMIN role by default
        Role gymAdminRole = roleService.findByName(RoleType.GYM_ADMIN);
        gym.getRoles().add(gymAdminRole);

        gymRepository.save(gym);
    }

    @Override
    public void loginGym(GymLoginRequestDto gymLoginRequestDto) {
        validateDto(gymLoginRequestDto);

        Gym gym = this.gymRepository.findByEmail(gymLoginRequestDto.getEmail()).orElseThrow(() ->
                new FitManageAppException("Account with this email does not exist.", ApiErrorCode.CONFLICT));

        if (!matchGymPassword(gymLoginRequestDto.getPassword(), gym.getPassword())) {
            throw new FitManageAppException("Gym passwords do not match.", ApiErrorCode.BAD_REQUEST);
        }
    }

    private boolean matchGymPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
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
