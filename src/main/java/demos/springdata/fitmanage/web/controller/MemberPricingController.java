package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import demos.springdata.fitmanage.service.MemberPricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class MemberPricingController {
    private final MemberPricingService pricingService;

    public MemberPricingController(MemberPricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<List<MemberPlanPriceDto>>> createPlans(@RequestBody List<MemberPlanPriceDto> planDtos) {
        String gymEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MemberPlanPriceDto> savedPlans = pricingService.createPlans(gymEmail, planDtos);
        return ResponseEntity.ok(ApiResponse.success(savedPlans));
    }


    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<MemberPlanPriceDto>>> getPlansAndPrices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String gymEmail = authentication.getName();

        List<MemberPlanPriceDto> plans = pricingService.getPlansAndPrices(gymEmail);

        return ResponseEntity.ok(ApiResponse.success(plans));
    }


}
