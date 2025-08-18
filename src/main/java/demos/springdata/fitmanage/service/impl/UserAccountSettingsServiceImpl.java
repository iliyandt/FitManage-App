package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;
import demos.springdata.fitmanage.domain.entity.GymAccountSettings;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.repository.GymAccountSettingsRepository;
import demos.springdata.fitmanage.service.UserAccountSettingsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserAccountSettingsServiceImpl implements UserAccountSettingsService {

    private final GymAccountSettingsRepository gymAccountSettingsRepository;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountSettingsServiceImpl.class);

    @Autowired
    public UserAccountSettingsServiceImpl(GymAccountSettingsRepository gymAccountSettingsRepository, ModelMapper modelMapper) {
        this.gymAccountSettingsRepository = gymAccountSettingsRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public AccountSettingsDto getUserSettings(Long id) {
        LOGGER.info("Fetching account settings for gym ID {}", id);
        return gymAccountSettingsRepository.findByUserId(id)
                .map(settings -> modelMapper.map(settings, AccountSettingsDto.class))
                .orElse(new AccountSettingsDto());
    }

    @Override
    @Transactional
    public AccountSettingsDto updateUserSettings(Long id, Map<String, Object> newSettings) {
        LOGGER.info("Updating account settings for gym ID {}", id);
        GymAccountSettings settings = gymAccountSettingsRepository.findByUserId(id)
                .orElseGet(() -> {
                    GymAccountSettings gymAccountSettings = new GymAccountSettings();
                    gymAccountSettings.setUser(new User());
                    return gymAccountSettings;
                });
        settings.getSettings().putAll(newSettings);
        GymAccountSettings savedSettings = gymAccountSettingsRepository.save(settings);

        return modelMapper.map(savedSettings, AccountSettingsDto.class);
    }
}
