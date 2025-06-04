package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;
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
    public List<GymAdminResponseDto> getAllGyms() {
        LOGGER.info("Retrieving all gyms");
        return this.gymRepository.findAll()
                .stream()
                .map(gym -> this.modelMapper.map(gym, GymAdminResponseDto.class))
                .toList();
    }
}
