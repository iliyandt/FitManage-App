package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.EnumOption;
import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserResponseDto;
import demos.springdata.fitmanage.service.MembershipPlanService;
import demos.springdata.fitmanage.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/membership")
@PreAuthorize("hasAnyAuthority('FACILITY_ADMIN', 'FACILITY_STAFF')")
public class MembershipController {
    private final MembershipService membershipService;
    private final MembershipPlanService membershipPlanService;

    public MembershipController(MembershipService membershipService, MembershipPlanService membershipPlanService) {
        this.membershipService = membershipService;
        this.membershipPlanService = membershipPlanService;
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateSubscription(
            @PathVariable Long memberId,
            @RequestBody @Valid MemberSubscriptionRequestDto dto) {

        UserResponseDto response = membershipService.initializeSubscription(memberId, dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/subscription-plans/customized-fields")
    public ResponseEntity<ApiResponse<List<EnumOption>>> getAllSubscriptionPlans() {
        List<MembershipPlanDto> planPriceDtoList = membershipPlanService.getPlansAndPricesAsPriceDto();
        List<EnumOption> enumOptions = planPriceDtoList.stream()
                .map(plan -> new EnumOption(plan.getSubscriptionPlan().getDisplayName(), plan.getSubscriptionPlan().toString()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(enumOptions));
    }
}
