package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.tenant.UserResponseDto;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenant")
@PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
public class TenantController {
    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/all")
    public ResponseEntity<@Valid List<UserResponseDto>> getAllTenants() {
        List<UserResponseDto> allTenants = tenantService.getAllTenants();
        return ResponseEntity.ok(allTenants);
    }
}
