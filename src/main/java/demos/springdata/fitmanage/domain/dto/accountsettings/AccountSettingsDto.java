package demos.springdata.fitmanage.domain.dto.accountsettings;

import java.util.Map;

public class AccountSettingsDto {
    private Map<String, Object> settings;

    public AccountSettingsDto() {}

    public AccountSettingsDto(Map<String, Object> settings) {
        this.settings = settings;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }
}
