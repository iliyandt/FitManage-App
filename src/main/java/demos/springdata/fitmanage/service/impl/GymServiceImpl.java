package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
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
import java.util.List;
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
        return this.gymRepository.findByEmail(email)
                .map(gym -> this.modelMapper.map(gym, GymSummaryDto.class));
    }

    @Override
    public GymMemberResponseDto addGymMemberToGym(GymMemberCreateRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String gymEmail = authentication.getName();
        LOGGER.info("Authenticated gym email: {}", gymEmail);
        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));


        return gymMemberService.createMemberForGym(gym, requestDto);
    }

}
