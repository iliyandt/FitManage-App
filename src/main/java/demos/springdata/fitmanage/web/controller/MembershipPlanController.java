package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanTableDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanPriceResponse;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.MembershipPlanService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership-plans")
@PreAuthorize("hasAuthority('ADMIN')")
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
    public ResponseEntity<ApiResponse<List<MembershipPlanDto>>> create(@RequestBody List<MembershipPlanDto> plansDto) {
        List<MembershipPlanDto> savedPlans = pricingService.createPlans(plansDto);
        return ResponseEntity.ok(ApiResponse.success(savedPlans));
    }

    @PatchMapping("/{planId}")
    public ResponseEntity<ApiResponse<MembershipPlanUpdateDto>> update(@PathVariable Long planId,
                                                                       @RequestBody @Valid MembershipPlanUpdateDto dto) {
        MembershipPlanUpdateDto updatedDto = membershipPlanService.updatePlanPrices(planId, dto);
        return ResponseEntity.ok(ApiResponse.success(updatedDto));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long planId) {
        membershipPlanService.deletePlan(planId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @GetMapping("table")
    public ResponseEntity<ApiResponse<TableResponseDto>> getPlansDataAsTable() {
        List<MembershipPlanDto> planPriceDtoList = membershipPlanService.getPlansData();
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/membership-plans", MembershipPlanTableDto.class));
        response.setColumns(TableColumnBuilder.buildColumns(MembershipPlanTableDto.class));
        response.setRows(tableHelper.buildRows(planPriceDtoList, tableHelper::buildRowMap));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{subscriptionPlan}/{employment}")
    public ResponseEntity<ApiResponse<PlanPriceResponse>> getPlanPrice(@PathVariable SubscriptionPlan subscriptionPlan, @PathVariable Employment employment) {
        PlanPriceResponse response = membershipPlanService.getPlanPrice(subscriptionPlan, employment);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
