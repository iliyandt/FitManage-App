package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.EnumOption;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberResponseDto;
import demos.springdata.fitmanage.service.MemberPricingService;
import demos.springdata.fitmanage.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/membership")
@PreAuthorize("hasAnyAuthority('FACILITY_ADMIN')")
public class MembershipController {
    private final MembershipService membershipService;
    private final MemberPricingService memberPricingService;

    public MembershipController(MembershipService membershipService, MemberPricingService memberPricingService) {
        this.membershipService = membershipService;
        this.memberPricingService = memberPricingService;
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> updateSubscription(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberSubscriptionRequestDto dto) {

        MemberResponseDto response = membershipService.initializeSubscription(memberId, dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/subscription_plans/customized_fields")
    public ResponseEntity<ApiResponse<List<EnumOption>>> getAllSubscriptionPlans() {
        List<MemberPlanPriceDto> planPriceDtoList = memberPricingService.getPlansAndPricesAsPriceDto();
        List<EnumOption> enumOptions = planPriceDtoList.stream()
                .map(plan -> new EnumOption(plan.getSubscriptionPlan().getDisplayName(), plan.getSubscriptionPlan().toString()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(enumOptions));
    }
}
