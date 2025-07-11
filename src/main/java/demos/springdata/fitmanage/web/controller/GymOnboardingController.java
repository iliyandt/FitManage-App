package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberRequestDto;
import demos.springdata.fitmanage.service.GymOnboardingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gym/onboarding")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class GymOnboardingController {
    private final GymOnboardingService gymOnboardingService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GymOnboardingController.class);

    public GymOnboardingController(GymOnboardingService gymOnboardingService) {
        this.gymOnboardingService = gymOnboardingService;
    }

    @PostMapping("/basic-info")
    public ResponseEntity<ApiResponse<String>> saveBasicInfo(@Valid @RequestBody GymBasicInfoDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        gymOnboardingService.updateGymBasicInfo(email, dto);
        return ResponseEntity.ok(ApiResponse.success("Basic info updated successfully"));
    }


    @PostMapping("/team")
    public ResponseEntity<ApiResponse<String>> addTeam(@RequestBody List<StaffMemberRequestDto> staffDtos) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        gymOnboardingService.registerGymTeamMembers(currentUser, staffDtos);
        return ResponseEntity.ok(ApiResponse.success("Team member added successfully"));
    }
}
