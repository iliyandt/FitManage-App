package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanTableDto;
import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import demos.springdata.fitmanage.domain.entity.User;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(MembershipPlanServiceImpl.class);
    private final UserService userService;

    @Autowired
    public MembershipPlanServiceImpl(MembershipPlanRepository membershipPlanRepository, ModelMapper modelMapper, UserService userService) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    public List<MembershipPlanDto> createPlans(Long id, List<MembershipPlanDto> plansDto) {
        User user = userService.findUserById(id);

        List<MembershipPlanDto> savedPlans = new ArrayList<>();

        for (MembershipPlanDto planDto : plansDto) {
            MembershipPlan plan = modelMapper.map(planDto, MembershipPlan.class);
            plan.setUser(user);
            MembershipPlan saved = membershipPlanRepository.save(plan);
            savedPlans.add(modelMapper.map(saved, MembershipPlanDto.class));
        }

        return savedPlans;
    }


    @Override
    public List<MembershipPlanDto> getPlansAndPrices() {
        List<MembershipPlan> plans = membershipPlanRepository.findAll();
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
}
