package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.service.GymService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    public ResponseEntity<@Valid GymSummaryDto> authenticatedGym() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info("Account information requested for gym: {}", authentication.getName());
        String currentGymEmail = authentication.getName();
        LOGGER.info("Account information for {} loaded successfully.", authentication.getName());
        GymSummaryDto currentGym = gymService.getGymByEmail(currentGymEmail).get();
        return ResponseEntity.ok(currentGym);
    }
}
