package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanPriceResponse;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;


import java.util.List;

public interface MembershipPlanService {
    List<MembershipPlanDto> createPlans(List<MembershipPlanDto> plansDto);
    List<MembershipPlanDto> getPlansData();
    MembershipPlanUpdateDto updatePlanPrices(Long planId, MembershipPlanUpdateDto dto);
    void deletePlan(Long planId);
    PlanPriceResponse getPlanPrice(SubscriptionPlan subscriptionPlan, Employment employment);
}
