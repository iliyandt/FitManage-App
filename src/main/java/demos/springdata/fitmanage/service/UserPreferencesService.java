package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.accountsettings.PreferencesResponse;
import demos.springdata.fitmanage.security.UserData;

import java.util.Map;

public interface UserPreferencesService {
    PreferencesResponse getPreferences(UserData user);
    PreferencesResponse upsert(UserData user, Map<String, Object> newSettings);
}
