package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymAccountSettings;
import demos.springdata.fitmanage.repository.GymAccountSettingsRepository;
import demos.springdata.fitmanage.service.GymAccountSettingsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class GymAccountSettingsServiceImpl implements GymAccountSettingsService {

    private final GymAccountSettingsRepository gymAccountSettingsRepository;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(GymAccountSettingsServiceImpl.class);

    @Autowired
    public GymAccountSettingsServiceImpl(GymAccountSettingsRepository gymAccountSettingsRepository, ModelMapper modelMapper) {
        this.gymAccountSettingsRepository = gymAccountSettingsRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public AccountSettingsDto getGymSettings(Long gymId) {
        LOGGER.info("Fetching account settings for gym ID {}", gymId);
        return gymAccountSettingsRepository.findByGymId(gymId)
                .map(settings -> modelMapper.map(settings, AccountSettingsDto.class))
                .orElse(new AccountSettingsDto());
    }

    @Override
    @Transactional
    public AccountSettingsDto updateGymSettings(Long gymId, Map<String, Object> newSettings) {
        LOGGER.info("Updating account settings for gym ID {}", gymId);
        GymAccountSettings settings = gymAccountSettingsRepository.findByGymId(gymId)
                .orElseGet(() -> {
                    GymAccountSettings gymAccountSettings = new GymAccountSettings();
                    gymAccountSettings.setGym(new Gym(gymId));
                    return gymAccountSettings;
                });
        settings.getSettings().putAll(newSettings);
        GymAccountSettings savedSettings = gymAccountSettingsRepository.save(settings);

        return modelMapper.map(savedSettings, AccountSettingsDto.class);
    }
}
