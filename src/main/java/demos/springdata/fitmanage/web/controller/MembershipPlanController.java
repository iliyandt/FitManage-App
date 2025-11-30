package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanTable;
import demos.springdata.fitmanage.domain.dto.membershipplan.UpdateRequest;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.PriceResponse;
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
import java.util.UUID;

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
    public ResponseEntity<ApiResponse<List<PlanDto>>> create(@RequestBody List<PlanDto> plansDto) {
        List<PlanDto> savedPlans = pricingService.createPlans(plansDto);
        return ResponseEntity.ok(ApiResponse.success(savedPlans));
    }

    @PatchMapping("/{planId}")
    public ResponseEntity<ApiResponse<UpdateRequest>> update(@PathVariable UUID planId,
                                                             @RequestBody @Valid UpdateRequest request) {
        UpdateRequest updatedDto = membershipPlanService.updatePlanPrices(planId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedDto));
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID planId) {
        membershipPlanService.deletePlan(planId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @GetMapping("table")
    public ResponseEntity<ApiResponse<TableResponseDto>> getPlansDataAsTable() {
        List<PlanDto> planPriceDtoList = membershipPlanService.getPlansData();
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/membership-plans", PlanTable.class));
        response.setColumns(TableColumnBuilder.buildColumns(PlanTable.class));
        response.setRows(tableHelper.buildRows(planPriceDtoList, tableHelper::buildRowMap));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MEMBER')")
    @GetMapping("/{subscriptionPlan}/{employment}")
    public ResponseEntity<ApiResponse<PriceResponse>> getPlanPrice(@PathVariable SubscriptionPlan subscriptionPlan, @PathVariable Employment employment) {
        PriceResponse response = membershipPlanService.getPlanPrice(subscriptionPlan, employment);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
