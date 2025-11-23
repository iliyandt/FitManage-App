package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.membershipplan.UpdateRequest;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.PriceResponse;
import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import org.springframework.http.HttpStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.MembershipPlanRepository;
import demos.springdata.fitmanage.service.MembershipPlanService;
import demos.springdata.fitmanage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final UserService userService;
    private final static Logger LOGGER = LoggerFactory.getLogger(MembershipPlanServiceImpl.class);

    @Autowired
    public MembershipPlanServiceImpl
            (
                    MembershipPlanRepository membershipPlanRepository,
                    UserService userService
            ) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.userService = userService;
    }

    @Override
    public List<PlanDto> createPlans(List<PlanDto> planDtoList) {
        Tenant tenant = userService.getCurrentUser().getTenant();

        List<MembershipPlan> createdPlans = new ArrayList<>();

        planDtoList.stream().map(request -> new MembershipPlan()
                .setSubscriptionPlan(request.getSubscriptionPlan())
                .setPrice(request.getPrice())
                .setStudentPrice(request.getStudentPrice())
                .setSeniorPrice(request.getSeniorPrice())
                .setHandicapPrice(request.getHandicapPrice())
                .setTenant(tenant)).forEach(plan -> {
            createdPlans.add(plan);
            membershipPlanRepository.save(plan);
        });

        return createdPlans.stream().map(plan -> PlanDto.builder()
                .id(plan.getId())
                .subscriptionPlan(plan.getSubscriptionPlan())
                .price(plan.getPrice())
                .studentPrice(plan.getStudentPrice())
                .seniorPrice(plan.getSeniorPrice())
                .handicapPrice(plan.getHandicapPrice())
                .build()).toList();
    }

    @Override
    public List<PlanDto> getPlansData() {
        Tenant tenant = userService.getCurrentUser().getTenant();
        List<MembershipPlan> plans = membershipPlanRepository.getMembershipPlansByTenant(tenant);

        return plans.stream()
                .map(plan -> {
                    return PlanDto.builder()
                            .id(plan.getId())
                            .subscriptionPlan(plan.getSubscriptionPlan())
                            .price(plan.getPrice())
                            .studentPrice(plan.getStudentPrice())
                            .seniorPrice(plan.getSeniorPrice())
                            .handicapPrice(plan.getHandicapPrice())
                            .build();
                }).toList();
    }

    @Override
    public UpdateRequest updatePlanPrices(UUID planId, UpdateRequest request) {
        MembershipPlan plan = membershipPlanRepository.getMembershipPlanById(planId);

        plan.setPrice(request.getPrice())
                .setStudentPrice(request.getStudentPrice())
                .setSeniorPrice(request.getSeniorPrice())
                .setHandicapPrice(request.getHandicapPrice());

        membershipPlanRepository.save(plan);
        return request;
    }

    @Override
    public void deletePlan(UUID planId) {
        MembershipPlan currentPlan = membershipPlanRepository.findById(planId)
                .orElseThrow(() -> new DamilSoftException(String.format("No plan with ID: %d", planId), HttpStatus.NOT_FOUND));
        LOGGER.info("Deleting plan with ID {}", planId);
        membershipPlanRepository.delete(currentPlan);
    }

    @Override
    public PriceResponse getPlanPrice(SubscriptionPlan subscriptionPlan, Employment employment) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        BigDecimal specificPlanPrice = membershipPlanRepository.findPriceByTenantAndSubscriptionPlanAndEmployment(tenant, subscriptionPlan, employment.name());
        return new PriceResponse(specificPlanPrice);
    }
}
