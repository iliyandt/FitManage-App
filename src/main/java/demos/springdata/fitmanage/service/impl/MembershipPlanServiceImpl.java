package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.membershipplan.UpdateRequest;
import demos.springdata.fitmanage.domain.dto.membershipplan.PlanRequest;
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
    public List<PlanRequest> createPlans(List<PlanRequest> requests) {
        Tenant tenant = userService.getCurrentUser().getTenant();

        List<PlanRequest> savedPlans = new ArrayList<>();

        for (PlanRequest request : requests) {
            MembershipPlan plan = modelMapper.map(request, MembershipPlan.class);
            plan.setTenant(tenant);
            MembershipPlan saved = membershipPlanRepository.save(plan);
            savedPlans.add(modelMapper.map(saved, PlanRequest.class));
        }

        return savedPlans;
    }


    @Override
    public List<PlanRequest> getPlansData() {
        Tenant tenant = userService.getCurrentUser().getTenant();
        List<MembershipPlan> plans = membershipPlanRepository.getMembershipPlansByTenant(tenant);

        return plans.stream()
                .map(p -> modelMapper.map(p, PlanRequest.class)).toList();
    }


    @Override
    public UpdateRequest updatePlanPrices(Long planId, UpdateRequest request) {
        MembershipPlan membershipPlan = membershipPlanRepository.getMembershipPlanById(planId);
        modelMapper.map(request, membershipPlan);
        membershipPlanRepository.save(membershipPlan);
        return request;
    }

    @Override
    public void deletePlan(Long planId) {
        MembershipPlan currentPlan = membershipPlanRepository.findById(planId)
                .orElseThrow(() -> new DamilSoftException(String.format("No plan with ID: %d", planId), HttpStatus.NOT_FOUND));

        LOGGER.info("Deleting plan with ID {}", planId);

        membershipPlanRepository.delete(currentPlan);

        LOGGER.info("Plan with ID {} deleted successfully", planId);
    }

    @Override
    public PriceResponse getPlanPrice(SubscriptionPlan subscriptionPlan, Employment employment) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        BigDecimal specificPlanPrice = membershipPlanRepository.findPriceByTenantAndSubscriptionPlanAndEmployment(tenant, subscriptionPlan, employment.name());
        return new PriceResponse(specificPlanPrice);
    }
}
