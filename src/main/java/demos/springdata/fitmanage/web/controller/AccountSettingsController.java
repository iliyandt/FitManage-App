package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.users.UserBaseResponseDto;
import demos.springdata.fitmanage.service.UserAccountSettingsService;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/settings")
@PreAuthorize("hasAnyAuthority('FACILITY_ADMIN', 'FACILITY_STAFF')")
public class AccountSettingsController {
    private final UserAccountSettingsService userAccountSettingsService;
    private final UserService userService;

    public AccountSettingsController(UserAccountSettingsService userAccountSettingsService, UserService userService) {
        this.userAccountSettingsService = userAccountSettingsService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountSettingsDto>> getCurrentAccountSettings() {
        String email = getCurrentGymEmail();
        UserBaseResponseDto profile = (UserBaseResponseDto) userService.getUserProfileByEmail(email);
        AccountSettingsDto settings = userAccountSettingsService.getUserSettings(profile.getId());
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping("update/me")
    public ResponseEntity<ApiResponse<AccountSettingsDto>> updateCurrentGymSettings(@RequestBody Map<String, Object> newSettings) {
        String authenticatedEmail = getCurrentGymEmail();
        UserBaseResponseDto profile = (UserBaseResponseDto) userService.getUserProfileByEmail(authenticatedEmail);

        AccountSettingsDto updatedSettings = userAccountSettingsService.updateUserSettings(profile.getId(), newSettings);

        return ResponseEntity.ok(ApiResponse.success(updatedSettings));
    }

    private String getCurrentGymEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
