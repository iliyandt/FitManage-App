package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.GymService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GymServiceImpl implements GymService {

    private final GymRepository gymRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(GymServiceImpl.class);

    @Autowired
    public GymServiceImpl(GymRepository gymRepository, ModelMapper modelMapper) {
        this.gymRepository = gymRepository;
        this.modelMapper = modelMapper;
    }




    @Override
    public Optional<Gym> findGymEntityByEmail(String email) {
        return gymRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public List<GymSummaryDto> getAllGyms() {
        LOGGER.info("Retrieving all gyms");
        return this.gymRepository.findAll()
                .stream()
                .map(gym -> this.modelMapper.map(gym, GymSummaryDto.class))
                .toList();
    }

    @Override
    public Optional<GymSummaryDto> getGymByEmail(String email) {
        LOGGER.info("Fetching gym with email: {}", email);
        Gym gym = gymRepository
                .findByEmailWithMembers(email).orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        GymSummaryDto dto = modelMapper.map(gym, GymSummaryDto.class);

        int membersCount = gym.getGymMembers().size();
        dto.setMembersCount(membersCount);

        return Optional.of(dto);
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




    private void updateGymDetails(GymBasicInfoDto gymDto, Gym gym) {
        modelMapper.map(gymDto, gym);
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
