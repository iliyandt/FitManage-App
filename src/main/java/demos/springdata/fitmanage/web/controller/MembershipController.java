package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberResponseDto;
import demos.springdata.fitmanage.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/membership")
public class MembershipController {
    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PatchMapping("/{memberId}/membership")
    public ResponseEntity<ApiResponse<MemberResponseDto>> updateSubscription(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberSubscriptionRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(membershipService.initializeSubscription(memberId, dto)));
    }

//    @GetMapping("/subscription_plans/customized_fields")
//    public ResponseEntity<ApiResponse<List<EnumOption>>> getAllSubscriptionPlans() {
//        List<MemberPlanPriceDto> planPriceDtoList = memberPricingService.getPlansAndPricesAsPriceDto();
//        List<EnumOption> enumOptions = planPriceDtoList.stream()
//                .map(plan -> new EnumOption(plan.getSubscriptionPlan().getDisplayName(), plan.getSubscriptionPlan().toString()))
//                .toList();
//        return ResponseEntity.ok(ApiResponse.success(enumOptions));
//    }
}
