package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.PredefinedStaffRole;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import demos.springdata.fitmanage.domain.entity.StaffRole;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.PredefinedStaffRoleRepository;
import demos.springdata.fitmanage.repository.StaffMemberRepository;
import demos.springdata.fitmanage.repository.StaffRoleRepository;
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
    private final PredefinedStaffRoleRepository predefinedStaffRoleRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(GymOnboardingServiceImpl.class);


    @Autowired
    public GymOnboardingServiceImpl(GymRepository gymRepository, StaffMemberRepository staffMemberRepository, PredefinedStaffRoleRepository predefinedStaffRoleRepository, StaffRoleRepository staffRoleRepository) {
        this.gymRepository = gymRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.predefinedStaffRoleRepository = predefinedStaffRoleRepository;
        this.staffRoleRepository = staffRoleRepository;
    }

    @Override
    public void updateGymBasicInfo(String email, GymBasicInfoDto dto) {
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
    public void registerGymStaffMembers(String gymEmail, List<StaffMemberRequestDto> dtos) {
        LOGGER.info("Adding {} team members to gym with email: {}", dtos.size(), gymEmail);

        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> {
                    LOGGER.warn("Gym with email {} not found when adding staff member", gymEmail);
                    return new FitManageAppException("Gym not found for email: " + gymEmail, ApiErrorCode.NOT_FOUND);
                });


        List<StaffMember> staffMembers = dtos.stream()
                .map(dto -> {
                    if (staffMemberRepository.existsByUsername(dto.getUsername())) {
                        LOGGER.error("Username {} already exists", dto.getUsername());
                        throw new FitManageAppException("Username already exists", ApiErrorCode.CONFLICT);
                    }

                    if (staffMemberRepository.existsByEmail(dto.getEmail())) {
                        LOGGER.error("Email {} already exists", dto.getEmail());
                        throw new FitManageAppException("Email already exists", ApiErrorCode.CONFLICT);
                    }

                    StaffRole staffRole = resolveStaffRole(dto, gym);

                    StaffMember staff = new StaffMember();
                    staff.setUsername(dto.getUsername());
                    staff.setFirstName(dto.getFirstName());
                    staff.setLastName(dto.getLastName());
                    staff.setEmail(dto.getEmail());
                    staff.setPhone(dto.getPhone());
                    staff.setGym(gym);
                    staff.setStaffRole(staffRole);

                    return staff;
                })
                .toList();

        staffMemberRepository.saveAll(staffMembers);
        LOGGER.info("Added {} staff members to gym: {}", staffMembers.size(), gymEmail);
    }

    private StaffRole resolveStaffRole(StaffMemberRequestDto dto, Gym gym) {

        if (dto.getStaffRoleId() != null) {
            return staffRoleRepository.findById(dto.getStaffRoleId())
                    .orElseThrow(() -> new FitManageAppException("Selected role not found", ApiErrorCode.NOT_FOUND));
        } else if (dto.getPredefinedRoleId() != null) {
            PredefinedStaffRole predefined = predefinedStaffRoleRepository.findById(dto.getPredefinedRoleId())
                    .orElseThrow(() -> new FitManageAppException("Predefined role not found", ApiErrorCode.NOT_FOUND));
        return staffRoleRepository.findByGymAndPredefinedStaffRole(gym, predefined)
                .orElseGet(() -> {
                    StaffRole role = new StaffRole();
                    role.setName(predefined.getName());
                    role.setGym(gym);
                    role.setPredefinedStaffRole(predefined);
                    return staffRoleRepository.save(role);
                });
    } else if (dto.getCustomRoleName() != null && !dto.getCustomRoleName().isBlank()) {
        return staffRoleRepository.findByNameAndGym(dto.getCustomRoleName(), gym)
                .orElseGet(() -> {
                    StaffRole newRole = new StaffRole();
                    newRole.setName(dto.getCustomRoleName());
                    newRole.setGym(gym);
                    return staffRoleRepository.save(newRole);
                });
        } else {
            throw new FitManageAppException("A staff role must be selected or created", ApiErrorCode.BAD_REQUEST);
        }
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
