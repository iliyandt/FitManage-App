package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gym.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gym.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.GymService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/v1/gym")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class GymController {
    private final GymService gymService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GymController.class);

    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GymSummaryDto>> authenticatedGym() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentGymEmail = authentication.getName();
        GymSummaryDto currentGym = gymService.getGymByEmail(currentGymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found for authenticated user", ApiErrorCode.NOT_FOUND));;
        return ResponseEntity.ok(ApiResponse.success(currentGym));
    }


    @PostMapping("/add_member")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> addGymMembers(@Valid @RequestBody GymMemberCreateRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        GymMemberResponseDto responseDto = gymService.addGymMemberToGym(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }
}
