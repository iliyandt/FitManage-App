package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.tenant.UserResponseDto;
import demos.springdata.fitmanage.service.UserAccountSettingsService;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.service.impl.AuthenticationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    public AccountSettingsController(UserAccountSettingsService userAccountSettingsService, UserService userService) {
        this.userAccountSettingsService = userAccountSettingsService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountSettingsDto>> getCurrentAccountSettings() {
        String currentGymEmail = getCurrentGymEmail();
        UserResponseDto gym = userService.getUserSummaryByEmail(currentGymEmail);
        AccountSettingsDto gymSettings = userAccountSettingsService.getUserSettings(gym.getId());
        return ResponseEntity.ok(ApiResponse.success(gymSettings));
    }

    @PutMapping("update/me")
    public ResponseEntity<ApiResponse<AccountSettingsDto>> updateCurrentGymSettings(@RequestBody Map<String, Object> newSettings) {
        String currentGymEmail = getCurrentGymEmail();
        UserResponseDto gym = userService.getUserSummaryByEmail(currentGymEmail);

        AccountSettingsDto updatedSettings = userAccountSettingsService.updateUserSettings(gym.getId(), newSettings);
        LOGGER.info("Updated settings: " + updatedSettings);

        return ResponseEntity.ok(ApiResponse.success(updatedSettings));
    }



    private String getCurrentGymEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
