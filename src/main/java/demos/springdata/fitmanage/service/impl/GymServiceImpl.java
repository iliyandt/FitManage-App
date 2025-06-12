package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.GymService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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
    public GymSummaryDto getGymByEmail(String email) {
        return this.gymRepository.findByEmail(email)
                .map(gym -> this.modelMapper.map(gym, GymSummaryDto.class))
                .orElseThrow(() -> new FitManageAppException("Gym not found.", ApiErrorCode.NOT_FOUND));
    }
}
