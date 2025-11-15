package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.analytics.DemographicDataResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.service.DemographicsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'ADMIN')")
public class DemographicsController {
   private final DemographicsService demographicsService;

    public DemographicsController(DemographicsService demographicsService) {
        this.demographicsService = demographicsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DemographicDataResponse>> getDemographicData() {
        DemographicDataResponse ratios = demographicsService.calculateDemographics();
        return ResponseEntity.ok(ApiResponse.success(ratios));
    }
}
