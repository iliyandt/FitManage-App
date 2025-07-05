package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.StaffMemberRepository;
import demos.springdata.fitmanage.service.GymOnboardingService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class GymOnboardingServiceImpl implements GymOnboardingService {
    private final GymRepository gymRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final static Logger LOGGER = LoggerFactory.getLogger(GymOnboardingServiceImpl.class);


    @Autowired
    public GymOnboardingServiceImpl(GymRepository gymRepository, StaffMemberRepository staffMemberRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.gymRepository = gymRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updateBasicInfo(String email, GymBasicInfoDto dto) {
        LOGGER.info("Updating basic info for gym with email: {}", email);
        Map<String, String> errors = new HashMap<>();
        Gym gym = getGymOrElseThrow(email);

        updateUsernameIfChanged(gym, dto.getUsername(), errors);
        updateGymDetails(dto, gym);

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }

        gymRepository.save(gym);
        LOGGER.info("Updated basic info for gym: {}", email);
    }


    @Override
    public void addTeamMembers(String gymEmail, List<StaffMemberRequestDto> dtos) {
        LOGGER.info("Adding {} team members to gym with email: {}", dtos.size(), gymEmail);
        List<StaffMember> staffMembers = dtos.stream()
                .map(dto -> {
                    if (staffMemberRepository.existsByUsername(dto.getUsername())) {
                        LOGGER.error("Username {} already exists", dto.getGymUsername());
                        throw new FitManageAppException("Username already exists", ApiErrorCode.CONFLICT);
                    } else if (staffMemberRepository.existsByEmail(dto.getEmail())) {
                        LOGGER.error("Email {} already exists", dto.getEmail());
                        throw new FitManageAppException("Email already exists", ApiErrorCode.CONFLICT);
                    }

                    Gym gym = gymRepository.findByUsername(dto.getGymUsername())
                            .orElseThrow(() -> {
                                LOGGER.warn("Gym with username {} not found when adding staff member", dto.getGymUsername());
                                return new FitManageAppException("Gym not found for username: " + dto.getGymUsername(), ApiErrorCode.NOT_FOUND);
                            });
                    StaffMember staff = modelMapper.map(dto, StaffMember.class);
                    staff.setPassword(passwordEncoder.encode(dto.getPassword()));
                    staff.setGym(gym);
                    return staff;
                })
                .toList();

        staffMemberRepository.saveAll(staffMembers);
        LOGGER.info("Added {} staff members to gym: {}", staffMembers.size(), gymEmail);
    }


    private static void updateGymDetails(GymBasicInfoDto dto, Gym gym) {
        gym.setEmail(dto.getEmail());
        gym.setPhone(dto.getPhone());
        gym.setAddress(dto.getAddress());
        gym.setCity(dto.getCity());
    }

    private void updateUsernameIfChanged(Gym gym, String username, Map<String, String> errors) {
        if (!gym.getActualUsername().equals(username)) {
            LOGGER.info("Gym username change detected: {} -> {}", gym.getActualUsername(), username);
            validateUsernameUniqueness(username, gym.getId(), errors);
            if (!errors.containsKey("username")) {
                gym.setUsername(username);
                LOGGER.info("Gym username updated to {}", username);
            }
        }
    }

    private void validateUsernameUniqueness(String username, Long currentGymId, Map<String, String> errors) {
        Optional<Gym> existing = gymRepository.findByUsername(username);
        if (existing.isPresent() && !existing.get().getId().equals(currentGymId)) {
            errors.put("username", "Username is taken");
        }
    }

    private Gym getGymOrElseThrow(String email) {
        return gymRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("Gym with email {} not found", email);
                    return new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND);
                });
    }
}
