package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;
import demos.springdata.fitmanage.service.GymService;
import demos.springdata.fitmanage.service.SuperAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {
    private final SuperAdminService superAdminService;
    private final GymService gymService;

    public SuperAdminController(SuperAdminService superAdminService, GymService gymService) {
        this.superAdminService = superAdminService;
        this.gymService = gymService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<GymAdminResponseDto>> getAllGyms() {
        List<GymAdminResponseDto> allGyms = gymService.getAllGyms();
        return ResponseEntity.ok(allGyms);
    }
}
