package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.membershipplan.UpdateRequest;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.PriceResponse;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;


import java.util.List;

public interface MembershipPlanService {
    List<PlanDto> createPlans(List<PlanDto> plansDto);
    List<PlanDto> getPlansData();
    UpdateRequest updatePlanPrices(Long planId, UpdateRequest dto);
    void deletePlan(Long planId);
    PriceResponse getPlanPrice(SubscriptionPlan subscriptionPlan, Employment employment);
}
