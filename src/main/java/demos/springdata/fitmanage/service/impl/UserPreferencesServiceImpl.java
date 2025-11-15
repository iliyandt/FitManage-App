package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.accountsettings.PreferencesResponse;
import demos.springdata.fitmanage.domain.entity.UserPreferences;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.repository.UserPreferencesRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.UserPreferencesService;
import demos.springdata.fitmanage.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesServiceImpl.class);

    @Autowired
    public UserPreferencesServiceImpl
            (
                    UserPreferencesRepository userPreferencesRepository,
                    UserService userService
            ) {
        this.userPreferencesRepository = userPreferencesRepository;
        this.userService = userService;
    }

    @Override
    public PreferencesResponse getPreferences(UserData user) {
        LOGGER.info("Fetching user preferences for account with ID {}", user.getId());
        
        Optional<UserPreferences> preferencesOpt = userPreferencesRepository.findByUserId(user.getId());

        if (preferencesOpt.isPresent()) {
            UserPreferences settings = preferencesOpt.get();
            return new PreferencesResponse(settings.getSettings());
        } else {
            return new PreferencesResponse(new HashMap<>());
        }
    }

    @Override
    @Transactional
    public PreferencesResponse upsert(UserData user, Map<String, Object> newSettings) {
        LOGGER.info("Updating account settings for account ID {}", user.getId());

        User cuurentUser = userService.findUserById(user.getId());

        UserPreferences preferences = userPreferencesRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserPreferences userPreferences = new UserPreferences();
                    userPreferences.setUser(cuurentUser);
                    return userPreferences;
                });

        preferences.getSettings().putAll(newSettings);

        UserPreferences savedSettings = userPreferencesRepository.save(preferences);

        return new PreferencesResponse(savedSettings.getSettings());
    }
}
