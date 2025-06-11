package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
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

import java.util.List;
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
        Gym gym = gymRepository.findByEmail(email)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        if (!gym.getUsername().equals(dto.getUsername())) {
            Optional<Gym> existing = gymRepository.findByUsername(dto.getUsername());
            if (existing.isPresent() && !existing.get().getId().equals(gym.getId())) {
                throw new FitManageAppException("Username is taken", ApiErrorCode.CONFLICT);
            }
            gym.setUsername(dto.getUsername());
        }

        gym.setEmail(dto.getEmail());
        gym.setPhone(dto.getPhone());
        gym.setAddress(dto.getAddress());
        gym.setCity(dto.getCity());

        gymRepository.save(gym);
        LOGGER.info("Updated basic info for gym: {}", email);
    }

    @Override
    public void addTeamMembers(String gymEmail, List<StaffMemberRequestDto> dtos) {

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
                            .orElseThrow(() -> new FitManageAppException("Gym not found for username: " + dto.getGymUsername(), ApiErrorCode.NOT_FOUND));

                    StaffMember staff = modelMapper.map(dto, StaffMember.class);
                    staff.setPassword(passwordEncoder.encode(dto.getPassword()));
                    staff.setGym(gym);
                    return staff;
                })
                .toList();

        staffMemberRepository.saveAll(staffMembers);
        LOGGER.info("Added {} staff members to gym: {}", staffMembers.size(), gymEmail);
    }
}
