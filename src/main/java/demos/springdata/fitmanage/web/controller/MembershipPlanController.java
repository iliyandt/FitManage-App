package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanDto;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.MembershipPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership-plans")
@PreAuthorize("hasAuthority('FACILITY_ADMIN')")
public class MembershipPlanController {
    private final MembershipPlanService pricingService;
    private final MembershipPlanService membershipPlanService;
    private final TableHelper tableHelper;

    public MembershipPlanController(MembershipPlanService pricingService, MembershipPlanService membershipPlanService, TableHelper tableHelper) {
        this.pricingService = pricingService;
        this.membershipPlanService = membershipPlanService;
        this.tableHelper = tableHelper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<MembershipPlanDto>>> createPlans(@RequestBody List<MembershipPlanDto> plansDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MembershipPlanDto> savedPlans = pricingService.createPlans(email, plansDto);
        return ResponseEntity.ok(ApiResponse.success(savedPlans));
    }


//    @GetMapping("/table")
//    public ResponseEntity<ApiResponse<TableResponseDto>> getPlansAndPrices() {
//        List<MemberPlansTableDto> planPriceDtoList = memberPricingService.getPlansAndPrices();
//        TableResponseDto response = new TableResponseDto();
//        response.setConfig(tableHelper.buildTableConfig("pricing/plans", MemberPlansTableDto.class));
//        response.setColumns(TableColumnBuilder.buildColumns(MemberPlansTableDto.class));
//        response.setRows(tableHelper.buildRows(planPriceDtoList, tableHelper::buildRowMap));
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }

    @PatchMapping("/{planId}")
    public ResponseEntity<ApiResponse<MembershipPlanUpdateDto>> editPlanPrices(@PathVariable Long planId,
                                                                               @RequestBody @Valid MembershipPlanUpdateDto dto) {
        MembershipPlanUpdateDto updatedDto = membershipPlanService.updatePlanPrices(planId, dto);
        return ResponseEntity.ok(ApiResponse.success(updatedDto));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long planId) {
        membershipPlanService.deletePlan(planId);
        //todo: add response dto for delete
        return ResponseEntity.ok(ApiResponse.success(null));
    }


}
