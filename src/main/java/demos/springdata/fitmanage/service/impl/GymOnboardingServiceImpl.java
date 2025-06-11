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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GymOnboardingServiceImpl implements GymOnboardingService {
    private final GymRepository gymRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(GymOnboardingServiceImpl.class);

    @Autowired
    public GymOnboardingServiceImpl(GymRepository gymRepository, StaffMemberRepository staffMemberRepository, ModelMapper modelMapper) {
        this.gymRepository = gymRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.modelMapper = modelMapper;
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
        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        List<StaffMember> staffMembers = dtos.stream()
                .map(dto -> {
                    StaffMember staff = modelMapper.map(dto, StaffMember.class);
                    staff.setGym(gym); // свържи всеки служител с gym-а
                    return staff;
                })
                .toList();

        staffMemberRepository.saveAll(staffMembers);
        LOGGER.info("Added {} staff members to gym: {}", staffMembers.size(), gymEmail);
    }
}
