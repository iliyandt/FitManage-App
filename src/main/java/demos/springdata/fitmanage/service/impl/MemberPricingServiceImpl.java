package demos.springdata.fitmanage.service.impl;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberPricingServiceImpl implements MemberPricingService {

    private final GymService gymService;
    private final MemberPricingRepository memberPricingRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MemberPricingServiceImpl(GymService gymService, MemberPricingRepository memberPricingRepository, ModelMapper modelMapper) {
        this.gymService = gymService;
        this.memberPricingRepository = memberPricingRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MemberPlanPriceDto> createPlans(String gymEmail, List<MemberPlanPriceDto> planDtos) {
        Gym gym = gymService.findGymEntityByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        List<MemberPlanPriceDto> savedDtos = new ArrayList<>();

        for (MemberPlanPriceDto dto : planDtos) {
            MemberPlanPrice entity = modelMapper.map(dto, MemberPlanPrice.class);
            entity.setGym(gym);
            MemberPlanPrice saved = memberPricingRepository.save(entity);
            savedDtos.add(modelMapper.map(saved, MemberPlanPriceDto.class));
        }

        return savedDtos;
    }

    @Override
    public List<MemberPlanPriceDto> getPlansAndPrices() {
        String gymEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Gym gym = gymService.findGymEntityByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        List<MemberPlanPriceDto> result = new ArrayList<>();

        for (SubscriptionPlan plan : SubscriptionPlan.values()) {
            MemberPlanPrice existing = memberPricingRepository
                    .findByGymIdAndPlanType(gym.getId(), plan)
                    .orElse(null);

            MemberPlanPriceDto dto = existing != null
                    ? modelMapper.map(existing, MemberPlanPriceDto.class)
                    : new MemberPlanPriceDto();

            dto.setPlanType(plan);
            result.add(dto);
        }

        return result;
    }
}
