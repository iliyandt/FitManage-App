package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;

import java.util.List;

public interface MemberPricingService {
    List<MemberPlanPriceDto> createPlans(String gymEmail, List<MemberPlanPriceDto> planDtos);
    List<MemberPlanPriceDto> getPlansAndPrices();
}
