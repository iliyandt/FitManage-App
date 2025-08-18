package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/super-admin")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
public class SuperAdminController {
    private final TenantService tenantService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SuperAdminController.class);

    public SuperAdminController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/all")
    public ResponseEntity<@Valid List<GymSummaryDto>> getAllGyms() {
        LOGGER.info("Information about all registered gyms requested");
        List<GymSummaryDto> allGyms = tenantService.getAllGyms();
        LOGGER.info("Information loaded successfully");
        return ResponseEntity.ok(allGyms);
    }
}
