package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanEditDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanTableDto;
import demos.springdata.fitmanage.domain.entity.MemberPlanPrice;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MemberPricingRepository;
import demos.springdata.fitmanage.service.TenantService;
import demos.springdata.fitmanage.service.MemberPricingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberPricingServiceImpl implements MemberPricingService {

    private final TenantService tenantService;
    private final MemberPricingRepository memberPricingRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(MemberPricingServiceImpl.class);

    @Autowired
    public MemberPricingServiceImpl(TenantService tenantService, MemberPricingRepository memberPricingRepository, ModelMapper modelMapper) {
        this.tenantService = tenantService;
        this.memberPricingRepository = memberPricingRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MemberPlanPriceDto> createPlans(String email, List<MemberPlanPriceDto> planDtos) {
        return planDtos;
    }


    @Override
    public List<MemberPlanTableDto> getPlansAndPrices() {
        return List.of();
    }

    @Override
    public List<MemberPlanPriceDto> getPlansAndPricesAsPriceDto() {
        return getPlansAndPrices().stream()
                .map(dto -> toDto(dto, MemberPlanPriceDto.class))
                .toList();
    }

    @Override
    public MemberPlanEditDto updatePlanPrices(Long planId, MemberPlanEditDto dto) {

        return dto;
    }

    @Override
    public void deletePlan(Long planId) {
        MemberPlanPrice currentPlan = memberPricingRepository.findById(planId)
                .orElseThrow(() -> new FitManageAppException(String.format("No plan with ID: %d", planId), ApiErrorCode.NOT_FOUND));

        LOGGER.info("Deleting plan with ID {}", planId);

        memberPricingRepository.delete(currentPlan);

        LOGGER.info("Plan with ID {} deleted successfully", planId);
    }


    private Tenant getTenantOrThrow(String gymEmail) {
        return tenantService.getTenantByEmail(gymEmail)
                .orElseThrow(() -> {
                    LOGGER.error("Gym not found with email: {}", gymEmail);
                    return new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND);
                });
    }

    private <T, U> U toDto(T entity, Class<U> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    private MemberPlanPrice toEntity(MemberPlanPriceDto dto, User user) {
        MemberPlanPrice entity = modelMapper.map(dto, MemberPlanPrice.class);
        entity.setUser(user);
        return entity;
    }

    private String getAuthenticatedGymEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }



}
