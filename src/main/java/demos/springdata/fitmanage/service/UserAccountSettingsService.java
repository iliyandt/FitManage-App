package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;

import java.util.Map;

public interface UserAccountSettingsService {
    AccountSettingsDto getUserSettings(Long userId);
    AccountSettingsDto updateUserSettings(Long userId, Map<String, Object> newSettings);
}
