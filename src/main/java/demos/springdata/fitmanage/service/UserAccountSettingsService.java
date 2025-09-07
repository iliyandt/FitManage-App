package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;

import java.util.Map;

public interface UserAccountSettingsService {
    AccountSettingsDto getUserSettings();
    AccountSettingsDto updateUserSettings(Map<String, Object> newSettings);
}
