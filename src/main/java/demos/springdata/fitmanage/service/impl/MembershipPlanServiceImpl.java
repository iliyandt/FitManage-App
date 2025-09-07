package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanDto;
import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MembershipPlanRepository;
import demos.springdata.fitmanage.security.CustomUserDetails;
import demos.springdata.fitmanage.service.MembershipPlanService;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final CurrentUserUtils currentUserUtils;
    private final static Logger LOGGER = LoggerFactory.getLogger(MembershipPlanServiceImpl.class);


    @Autowired
    public MembershipPlanServiceImpl
            (
                    MembershipPlanRepository membershipPlanRepository,
                    ModelMapper modelMapper,
                    UserService userService, CurrentUserUtils currentUserUtils
            ) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.currentUserUtils = currentUserUtils;
    }

    @Override
    public List<MembershipPlanDto> createPlans(List<MembershipPlanDto> plansDto) {
        User user = currentUserUtils.getCurrentUser();

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
        Long userId = currentUserUtils.getCurrentUser().getId();
        List<MembershipPlan> plans = membershipPlanRepository.getMembershipPlansByUser_Id(userId);

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
}
