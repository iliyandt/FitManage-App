package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gym.*;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(path = "/api/v1/gym")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class UserController {
    private final TenantService tenantService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    public UserController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GymSummaryDto>> authenticatedGym() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentGymEmail = authentication.getName();
        GymSummaryDto currentGym = tenantService.getGymByEmail(currentGymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found for authenticated user", ApiErrorCode.NOT_FOUND));
        ;
        return ResponseEntity.ok(ApiResponse.success(currentGym));
    }


    @PostMapping("/basic-info")
    public ResponseEntity<ApiResponse<String>> saveBasicInfo(@Valid @RequestBody GymBasicInfoDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        tenantService.updateTenantBasicInfo(email, dto);
        return ResponseEntity.ok(ApiResponse.success("Basic info updated successfully"));
    }


}
