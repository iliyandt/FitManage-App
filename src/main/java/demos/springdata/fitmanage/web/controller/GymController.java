package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;
import demos.springdata.fitmanage.service.GymService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping(path = "/api/v1/gym")
@PreAuthorize("hasRole('GYM_ADMIN')")
public class GymController {
    private final GymService gymService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GymController.class);
    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping("/me")
    public ResponseEntity<GymAdminResponseDto> authenticatedGym() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentGymEmail = authentication.getName();

        GymAdminResponseDto currentGym = gymService.getGymByEmail(currentGymEmail);
        return ResponseEntity.ok(currentGym);
    }
}
