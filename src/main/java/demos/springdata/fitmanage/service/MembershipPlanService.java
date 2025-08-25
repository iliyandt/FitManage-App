package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanDto;


import java.util.List;

public interface MembershipPlanService {
    List<MembershipPlanDto> createPlans(Long id, List<MembershipPlanDto> plansDto);
    List<MembershipPlanDto> getPlansAndPrices();
    List<MembershipPlanDto> getPlansAndPricesAsPriceDto();
    MembershipPlanUpdateDto updatePlanPrices(Long planId, MembershipPlanUpdateDto dto);
    void deletePlan(Long planId);
}
