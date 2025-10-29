package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanPriceResponse;
import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MembershipPlanRepository;
import demos.springdata.fitmanage.service.MembershipPlanService;
import demos.springdata.fitmanage.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(MembershipPlanServiceImpl.class);
    private final UserService userService;


    @Autowired
    public MembershipPlanServiceImpl
            (
                    MembershipPlanRepository membershipPlanRepository,
                    ModelMapper modelMapper,

                    UserService userService) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    public List<MembershipPlanDto> createPlans(List<MembershipPlanDto> plansDto) {
        Tenant tenant = userService.getCurrentUser().getTenant();

        List<MembershipPlanDto> savedPlans = new ArrayList<>();

        for (MembershipPlanDto planDto : plansDto) {
            MembershipPlan plan = modelMapper.map(planDto, MembershipPlan.class);
            plan.setTenant(tenant);
            MembershipPlan saved = membershipPlanRepository.save(plan);
            savedPlans.add(modelMapper.map(saved, MembershipPlanDto.class));
        }

        return savedPlans;
    }


    @Override
    public List<MembershipPlanDto> getPlansAndPrices() {
        Tenant tenant = userService.getCurrentUser().getTenant();
        List<MembershipPlan> plans = membershipPlanRepository.getMembershipPlansByTenant(tenant);

        return plans.stream()
                .map(p -> modelMapper.map(p, MembershipPlanDto.class)).toList();
    }

    @Override
    public List<MembershipPlanDto> getPlansAndPricesAsPriceDto() {
        return getPlansAndPrices().stream()
                .map(dto -> modelMapper.map(dto, MembershipPlanDto.class))
                .toList();
    }

    @Override
    public MembershipPlanUpdateDto updatePlanPrices(Long planId, MembershipPlanUpdateDto dto) {
        MembershipPlan membershipPlan = membershipPlanRepository.getMembershipPlanById(planId);
        modelMapper.map(dto, membershipPlan);
        membershipPlanRepository.save(membershipPlan);
        return dto;
    }

    @Override
    public void deletePlan(Long planId) {
        MembershipPlan currentPlan = membershipPlanRepository.findById(planId)
                .orElseThrow(() -> new FitManageAppException(String.format("No plan with ID: %d", planId), ApiErrorCode.NOT_FOUND));

        LOGGER.info("Deleting plan with ID {}", planId);

        membershipPlanRepository.delete(currentPlan);

        LOGGER.info("Plan with ID {} deleted successfully", planId);
    }

    @Override
    public PlanPriceResponse getCurrentPlanPrice(SubscriptionPlan subscriptionPlan, Employment employment) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        BigDecimal specificPlanPrice = membershipPlanRepository.findPriceByTenantAndSubscriptionPlanAndEmployment(tenant, subscriptionPlan, employment.name());
        PlanPriceResponse planPriceResponse = new PlanPriceResponse();
        return planPriceResponse.setPrice(specificPlanPrice);
    }
}
