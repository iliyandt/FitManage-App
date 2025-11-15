package demos.springdata.fitmanage.domain.dto.accountsettings;

import java.util.Map;


public record PreferencesResponse(Map<String, Object> settings) {

    public PreferencesResponse(Map<String, Object> settings) {
        this.settings = settings;
    }

}
