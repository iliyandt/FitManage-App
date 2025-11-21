package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.EnumOption;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanDto;
import demos.springdata.fitmanage.domain.dto.member.request.SubscriptionRequest;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.service.MembershipPlanService;
import demos.springdata.fitmanage.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memberships")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
public class MembershipController {
    private final MembershipService membershipService;
    private final MembershipPlanService membershipPlanService;

    public MembershipController(MembershipService membershipService, MembershipPlanService membershipPlanService) {
        this.membershipService = membershipService;
        this.membershipPlanService = membershipPlanService;
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long memberId,
            @RequestBody @Valid SubscriptionRequest dto) {
        UserResponse response = membershipService.setupMembershipPlan(memberId, dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EnumOption>>> getPlansForTenant() {
        List<PlanDto> planPriceDtoList = membershipPlanService.getPlansData();
        List<EnumOption> enumOptions = planPriceDtoList.stream()
                .map(plan -> new EnumOption(plan.getSubscriptionPlan().getDisplayName(), plan.getSubscriptionPlan().toString()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(enumOptions));
    }
}
