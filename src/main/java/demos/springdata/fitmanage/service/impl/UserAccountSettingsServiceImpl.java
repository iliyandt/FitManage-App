package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;
import demos.springdata.fitmanage.domain.entity.AccountSettings;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.repository.AccountSettingsRepository;
import demos.springdata.fitmanage.service.UserAccountSettingsService;
import demos.springdata.fitmanage.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserAccountSettingsServiceImpl implements UserAccountSettingsService {

    private final AccountSettingsRepository accountSettingsRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountSettingsServiceImpl.class);

    @Autowired
    public UserAccountSettingsServiceImpl(AccountSettingsRepository accountSettingsRepository, ModelMapper modelMapper, UserService userService) {
        this.accountSettingsRepository = accountSettingsRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    public AccountSettingsDto getUserSettings(Long id) {
        LOGGER.info("Fetching account settings for account with ID {}", id);
        return accountSettingsRepository.findByUserId(id)
                .map(settings -> modelMapper.map(settings, AccountSettingsDto.class))
                .orElse(new AccountSettingsDto());
    }

    @Override
    @Transactional
    public AccountSettingsDto updateUserSettings(Long id, Map<String, Object> newSettings) {
        LOGGER.info("Updating account settings for account ID {}", id);
        User user = userService.findUserById(id);

        AccountSettings settings = accountSettingsRepository.findByUserId(id)
                .orElseGet(() -> {
                    AccountSettings accountSettings = new AccountSettings();
                    accountSettings.setUser(user);
                    return accountSettings;
                });
        settings.getSettings().putAll(newSettings);
        AccountSettings savedSettings = accountSettingsRepository.save(settings);

        return modelMapper.map(savedSettings, AccountSettingsDto.class);
    }
}
