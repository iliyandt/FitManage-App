package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanEditDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.MemberPlanPrice;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MemberPricingRepository;
import demos.springdata.fitmanage.service.GymService;
import demos.springdata.fitmanage.service.MemberPricingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MemberPricingServiceImpl implements MemberPricingService {

    private final GymService gymService;
    private final MemberPricingRepository memberPricingRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(MemberPricingServiceImpl.class);

    @Autowired
    public MemberPricingServiceImpl(GymService gymService, MemberPricingRepository memberPricingRepository, ModelMapper modelMapper) {
        this.gymService = gymService;
        this.memberPricingRepository = memberPricingRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MemberPlanPriceDto> createPlans(String gymEmail, List<MemberPlanPriceDto> planDtos) {
        Gym gym = getGymOrThrow(gymEmail);
        LOGGER.info("Creating {} member pricing plans for gym: {}", planDtos.size(), gymEmail);

        List<MemberPlanPriceDto> savedDtos = planDtos.stream()
                .map(dto -> toEntity(dto, gym))
                .map(memberPricingRepository::save)
                .map(this::toDto)
                .toList();

        LOGGER.info("Successfully created {} plans for gym '{}'", savedDtos.size(), gymEmail);
        return savedDtos;
    }


    @Override
    public List<MemberPlanPriceDto> getPlansAndPrices() {
        Gym gym = getAuthenticatedGymOrThrow();
        LOGGER.info("Fetching plans and prices for gym: {}", gym.getEmail());

        if (!memberPricingRepository.existsByGymId(gym.getId())) {
            LOGGER.warn("No pricing plans found for gym: {}", gym.getEmail());
            return Collections.emptyList();
        }

        List<MemberPlanPriceDto> result = new ArrayList<>();

        for (SubscriptionPlan plan : SubscriptionPlan.values()) {
            memberPricingRepository.findByGymIdAndSubscriptionPlan(gym.getId(), plan)
                    .ifPresent(entity -> {
                        MemberPlanPriceDto dto = toDto(entity);
                        dto.setSubscriptionPlan(plan);
                        result.add(dto);
                    });
        }

        LOGGER.debug("Returning {} plans for gym '{}'", result.size(), gym.getEmail());
        return result;
    }

    @Override
    public MemberPlanEditDto updatePlanPrices(Long planId, MemberPlanEditDto dto) {
        Gym gym = getAuthenticatedGymOrThrow();
        LOGGER.info("Updating plan ID {} for gym '{}'", planId, gym.getEmail());


        MemberPlanPrice entity = memberPricingRepository.findById(planId)
                .orElseThrow(() -> new FitManageAppException("Plan not found", ApiErrorCode.NOT_FOUND));

        if (!entity.getGym().getId().equals(gym.getId())) {
            LOGGER.error("Unauthorized attempt to update plan ID {} by gym '{}'", planId, gym.getEmail());
            throw new FitManageAppException("Unauthorized access to plan", ApiErrorCode.UNAUTHORIZED);
        }

        modelMapper.map(dto, entity);

        MemberPlanPrice updatedEntity = memberPricingRepository.save(entity);

        LOGGER.info("Successfully updated plan ID {} for gym '{}'", planId, gym.getEmail());
        return modelMapper.map(updatedEntity, MemberPlanEditDto.class);
    }



    private Gym getGymOrThrow(String gymEmail) {
        return gymService.findGymEntityByEmail(gymEmail)
                .orElseThrow(() -> {
                    LOGGER.error("Gym not found with email: {}", gymEmail);
                    return new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND);
                });
    }

    private MemberPlanPriceDto toDto(MemberPlanPrice entity) {
        return modelMapper.map(entity, MemberPlanPriceDto.class);
    }

    private MemberPlanPrice toEntity(MemberPlanPriceDto dto, Gym gym) {
        MemberPlanPrice entity = modelMapper.map(dto, MemberPlanPrice.class);
        entity.setGym(gym);
        return entity;
    }

    private String getAuthenticatedGymEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Gym getAuthenticatedGymOrThrow() {
        return getGymOrThrow(getAuthenticatedGymEmail());
    }


}
