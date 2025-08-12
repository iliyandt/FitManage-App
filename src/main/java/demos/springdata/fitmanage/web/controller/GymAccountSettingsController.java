package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.accountsettings.AccountSettingsDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.service.GymAccountSettingsService;
import demos.springdata.fitmanage.service.GymService;
import demos.springdata.fitmanage.service.impl.AuthenticationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/gym/account-settings")
public class GymAccountSettingsController {
    private final GymAccountSettingsService gymAccountSettingsService;
    private final GymService gymService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    public GymAccountSettingsController(GymAccountSettingsService gymAccountSettingsService, GymService gymService) {
        this.gymAccountSettingsService = gymAccountSettingsService;
        this.gymService = gymService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountSettingsDto>> getCurrentGymSettings() {

        String currentGymEmail = getCurrentGymEmail();
        Optional<GymSummaryDto> gym = gymService.getGymByEmail(currentGymEmail);
        AccountSettingsDto gymSettings = gymAccountSettingsService.getGymSettings(gym.get().getId());
        return ResponseEntity.ok(ApiResponse.success(gymSettings));
    }

    @PutMapping("update/me")
    public ResponseEntity<ApiResponse<AccountSettingsDto>> updateCurrentGymSettings(@RequestBody Map<String, Object> newSettings) {
        String currentGymEmail = getCurrentGymEmail();
        Optional<GymSummaryDto> gym = gymService.getGymByEmail(currentGymEmail);


        AccountSettingsDto updatedSettings = gymAccountSettingsService.updateGymSettings(gym.get().getId(), newSettings);

        LOGGER.info("Updated settings: " + updatedSettings);
        return ResponseEntity.ok(ApiResponse.success(updatedSettings));
    }

    private String getCurrentGymEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
