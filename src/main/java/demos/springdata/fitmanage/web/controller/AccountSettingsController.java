package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.service.UserAccountSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/settings")
@PreAuthorize("hasAnyAuthority('FACILITY_ADMIN', 'FACILITY_STAFF', 'FACILITY_MEMBER')")
public class AccountSettingsController {
    private final UserAccountSettingsService userAccountSettingsService;

    public AccountSettingsController(UserAccountSettingsService userAccountSettingsService) {
        this.userAccountSettingsService = userAccountSettingsService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountSettingsDto>> getCurrentAccountSettings() {
        AccountSettingsDto settings = userAccountSettingsService.getUserSettings();
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping("update/me")
    public ResponseEntity<ApiResponse<AccountSettingsDto>> updateCurrentGymSettings(@RequestBody Map<String, Object> newSettings) {
        AccountSettingsDto updatedSettings = userAccountSettingsService.updateUserSettings(newSettings);
        return ResponseEntity.ok(ApiResponse.success(updatedSettings));
    }
}
