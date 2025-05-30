package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.GymAdminResponseDto;
import demos.springdata.fitmanage.service.GymService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/all")
    public ResponseEntity<List<GymAdminResponseDto>> getAllGyms() {
        List<GymAdminResponseDto> allGyms = gymService.getAllGyms();
        return ResponseEntity.ok(allGyms);
    }
}
