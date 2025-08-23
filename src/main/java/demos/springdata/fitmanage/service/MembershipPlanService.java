package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanTableDto;


import java.util.List;

public interface MembershipPlanService {
    List<MembershipPlanDto> createPlans(String gymEmail, List<MembershipPlanDto> planDtos);
    List<MemberPlanTableDto> getPlansAndPrices();
    List<MembershipPlanDto> getPlansAndPricesAsPriceDto();
    MembershipPlanUpdateDto updatePlanPrices(Long planId, MembershipPlanUpdateDto dto);
    void deletePlan(Long planId);
}
