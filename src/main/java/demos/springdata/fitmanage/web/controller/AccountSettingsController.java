package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.accountsettings.PreferencesResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.UserPreferencesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/settings")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'MEMBER')")
public class AccountSettingsController {
    private final UserPreferencesService userPreferencesService;

    public AccountSettingsController(UserPreferencesService userPreferencesService) {
        this.userPreferencesService = userPreferencesService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PreferencesResponse>> getCurrentAccountSettings(@AuthenticationPrincipal UserData user) {
        PreferencesResponse settings = userPreferencesService.getPreferences(user);
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<PreferencesResponse>> updateCurrentGymSettings(@AuthenticationPrincipal UserData user, @RequestBody Map<String, Object> newSettings) {
        PreferencesResponse updatedSettings = userPreferencesService.upsert(user, newSettings);
        return ResponseEntity.ok(ApiResponse.success(updatedSettings));
    }
}
