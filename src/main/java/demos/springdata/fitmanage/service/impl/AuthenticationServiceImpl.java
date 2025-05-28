package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.AuthenticationService;
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

    @Autowired
    public AuthenticationServiceImpl(GymRepository gymRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, ValidationUtil validationUtil) {
        this.gymRepository = gymRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.validationUtil = validationUtil;
    }

    @Override
    public void registerGym(GymRegistrationRequestDto gymRegistrationDto) {

        if (!validationUtil.isValid(gymRegistrationDto)) {
            Set<ConstraintViolation<GymRegistrationRequestDto>> violations =
                    validationUtil.violations(gymRegistrationDto);

            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            throw new FitManageAppException(errorMessage, ApiErrorCode.BAD_REQUEST);
        }

        if (gymRepository.findByName(gymRegistrationDto.getName()).isPresent()) {
            throw new FitManageAppException("Gym with this name already exists", ApiErrorCode.CONFLICT);
        }

        if (gymRepository.findByEmail(gymRegistrationDto.getEmail()).isPresent()) {
            throw new FitManageAppException("Email is already registered", ApiErrorCode.CONFLICT);
        }

        Gym gym = mapGym(gymRegistrationDto);
        encryptGymPassword(gym);
        gym.setCreatedAt(LocalDateTime.now());
        gymRepository.save(gym);
    }

    @Override
    public void loginUser(GymRegistrationRequestDto gymRegistrationRequestDto) {
        //todo
    }

    private void encryptGymPassword(Gym gym) {
        String encryptedPassword = passwordEncoder.encode(gym.getPassword());
        gym.setPassword(encryptedPassword);
    }

    private Gym mapGym(GymRegistrationRequestDto gymRegistrationDto) {
        return modelMapper.map(gymRegistrationDto, Gym.class);
    }
}
