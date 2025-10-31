package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.analytics.UserRatioAnalyticsDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'ADMIN')")
public class AnalyticsController {
   private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserRatioAnalyticsDto>> getUserRatios() {
        UserRatioAnalyticsDto ratios = analyticsService.calculateUserRatios();
        return ResponseEntity.ok(ApiResponse.success(ratios));
    }
}
