package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;

import java.util.Map;

public interface GymAccountSettingsService {
    AccountSettingsDto getGymSettings(Long userId);
    AccountSettingsDto updateGymSettings(Long userId, Map<String, Object> newSettings);
}
