package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.GymMemberService;
import demos.springdata.fitmanage.service.GymService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GymServiceImpl implements GymService {

    private final GymRepository gymRepository;
    private final GymMemberService gymMemberService;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(GymServiceImpl.class);

    @Autowired
    public GymServiceImpl(GymRepository gymRepository, ModelMapper modelMapper, GymMemberService gymMemberService) {
        this.gymRepository = gymRepository;
        this.modelMapper = modelMapper;
        this.gymMemberService = gymMemberService;
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
        return this.gymRepository.findByEmail(email)
                .map(gym -> this.modelMapper.map(gym, GymSummaryDto.class));
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
    public GymMemberResponseDto registerNewMemberToGym(GymMemberCreateRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String gymEmail = authentication.getName();
        LOGGER.info("Authenticated gym email: {}", gymEmail);
        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> {
                    LOGGER.warn("Gym with email {} not found when adding member", gymEmail);
                    return new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND);
                });

        GymMemberResponseDto memberResponse = gymMemberService.registerMemberToGym(gym, requestDto);
        LOGGER.info("Successfully added member with ID {} to gym '{}'", memberResponse.getId(), gym.getEmail());

        return memberResponse;
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
