package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;
import demos.springdata.fitmanage.service.GymService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping(path = "/api/v1/gym")
public class GymController {
    private final GymService gymService;

    public GymController(GymService gymService) {
        this.gymService = gymService;
    }


    @GetMapping("/me")
    public ResponseEntity<GymAdminResponseDto> authenticatedGym() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        System.out.println("Principal class: " + principal.getClass().getName());
        System.out.println("Principal: " + principal.toString());

        GymAdminResponseDto currentGym = (GymAdminResponseDto) authentication.getPrincipal();
        return ResponseEntity.ok(currentGym);
    }


    @GetMapping("/all")
    public ResponseEntity<List<GymAdminResponseDto>> getAllGyms() {
        List<GymAdminResponseDto> allGyms = gymService.getAllGyms();
        return ResponseEntity.ok(allGyms);
    }
}
