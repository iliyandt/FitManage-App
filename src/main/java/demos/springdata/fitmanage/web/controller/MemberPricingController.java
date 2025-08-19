package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.MemberPricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing")
@PreAuthorize("hasAuthority('FACILITY_ADMIN')")
public class MemberPricingController {
    private final MemberPricingService pricingService;
    private final MemberPricingService memberPricingService;
    private final TableHelper tableHelper;

    public MemberPricingController(MemberPricingService pricingService, MemberPricingService memberPricingService, TableHelper tableHelper) {
        this.pricingService = pricingService;
        this.memberPricingService = memberPricingService;
        this.tableHelper = tableHelper;
    }

    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<List<MemberPlanPriceDto>>> createPlans(@RequestBody List<MemberPlanPriceDto> planDtos) {
        String gymEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MemberPlanPriceDto> savedPlans = pricingService.createPlans(gymEmail, planDtos);
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
//
//    @PatchMapping("/plans/{planId}")
//    public ResponseEntity<ApiResponse<MemberPlanEditDto>> editPlanPrices(@PathVariable Long planId,
//                                                                         @RequestBody @Valid MemberPlanEditDto dto) {
//        MemberPlanEditDto updatedDto = memberPricingService.updatePlanPrices(planId, dto);
//        return ResponseEntity.ok(ApiResponse.success(updatedDto));
//    }

    @DeleteMapping("/plans/{planId}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long planId) {
        memberPricingService.deletePlan(planId);
        //todo: add response dto for delete
        return ResponseEntity.ok(ApiResponse.success(null));
    }


}
