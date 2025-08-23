package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanUpdateDto;
import demos.springdata.fitmanage.domain.dto.pricing.MembershipPlanDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanTableDto;
import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MembershipPlanRepository;
import demos.springdata.fitmanage.service.MembershipPlanService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(MembershipPlanServiceImpl.class);

    @Autowired
    public MembershipPlanServiceImpl(MembershipPlanRepository membershipPlanRepository, ModelMapper modelMapper) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MembershipPlanDto> createPlans(String email, List<MembershipPlanDto> plansDto) {

        return plansDto;
    }


    @Override
    public List<MemberPlanTableDto> getPlansAndPrices() {
        return List.of();
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
