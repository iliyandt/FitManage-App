package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanEditDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlansTableDto;


import java.util.List;

public interface MemberPricingService {
    List<MemberPlanPriceDto> createPlans(String gymEmail, List<MemberPlanPriceDto> planDtos);
//    List<MemberPlansTableDto> getPlansAndPrices();
//    List<MemberPlanPriceDto> getPlansAndPricesAsPriceDto();
//    MemberPlanEditDto updatePlanPrices(Long planId, MemberPlanEditDto dto);
    void deletePlan(Long planId);
}
