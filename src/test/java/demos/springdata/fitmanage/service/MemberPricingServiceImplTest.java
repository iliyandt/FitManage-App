package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanEditDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.MemberPlanPrice;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MemberPricingRepository;
import demos.springdata.fitmanage.service.impl.MemberPricingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberPricingServiceImplTest {

    @Mock
    private GymService gymService;

    @Mock
    private MemberPricingRepository memberPricingRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MemberPricingServiceImpl service;

    private Gym gym;

    @BeforeEach
    void setUp() {
        gym = new Gym();
        gym.setEmail("gym@example.com");
        gym.setPassword("pass");
        gym.setUsername("gymuser");
        // Simulate authentication context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("gym@example.com", "pwd")
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createPlans_savesEachPlanAndReturnsMappedDtos() {
        List<MemberPlanPriceDto> inputDtos = List.of(
                new MemberPlanPriceDto().setSubscriptionPlan(SubscriptionPlan.MONTHLY),
                new MemberPlanPriceDto().setSubscriptionPlan(SubscriptionPlan.ANNUAL)
        );

        when(gymService.findGymEntityByEmail("gym@example.com")).thenReturn(Optional.of(gym));

        for (MemberPlanPriceDto dto : inputDtos) {
            MemberPlanPrice entity = new MemberPlanPrice()
                    .setSubscriptionPlan(dto.getSubscriptionPlan());
            MemberPlanPrice saved = new MemberPlanPrice()
                    .setSubscriptionPlan(dto.getSubscriptionPlan());
            when(modelMapper.map(dto, MemberPlanPrice.class)).thenReturn(entity);
            when(memberPricingRepository.save(entity)).thenReturn(saved);
            MemberPlanPriceDto mappedBack = new MemberPlanPriceDto()
                    .setSubscriptionPlan(dto.getSubscriptionPlan());

            when(modelMapper.map(saved, MemberPlanPriceDto.class)).thenReturn(mappedBack);
        }

        List<MemberPlanPriceDto> result = service.createPlans("gym@example.com", inputDtos);

        assertEquals(2, result.size());
        verify(memberPricingRepository, times(2)).save(any(MemberPlanPrice.class));
    }

    @Test
    void getPlansAndPrices_returnsOnlyExistingPlans() {
        gym.setId(42L);
        when(gymService.findGymEntityByEmail("gym@example.com")).thenReturn(Optional.of(gym));
        when(memberPricingRepository.existsByGymId(42L)).thenReturn(true);

        MemberPlanPrice monthly = new MemberPlanPrice().setSubscriptionPlan(SubscriptionPlan.MONTHLY);
        MemberPlanPrice annual = new MemberPlanPrice().setSubscriptionPlan(SubscriptionPlan.ANNUAL);

        when(memberPricingRepository.findByGymIdAndSubscriptionPlan(42L, SubscriptionPlan.MONTHLY))
                .thenReturn(Optional.of(monthly));
        when(memberPricingRepository.findByGymIdAndSubscriptionPlan(42L, SubscriptionPlan.ANNUAL))
                .thenReturn(Optional.of(annual));

        for (SubscriptionPlan plan : SubscriptionPlan.values()) {
            if (plan != SubscriptionPlan.MONTHLY && plan != SubscriptionPlan.ANNUAL) {
                when(memberPricingRepository.findByGymIdAndSubscriptionPlan(42L, plan))
                        .thenReturn(Optional.empty());
            }
        }

        when(modelMapper.map(eq(monthly), eq(MemberPlanPriceDto.class)))
                .thenReturn(new MemberPlanPriceDto().setSubscriptionPlan(SubscriptionPlan.MONTHLY));
        when(modelMapper.map(eq(annual), eq(MemberPlanPriceDto.class)))
                .thenReturn(new MemberPlanPriceDto().setSubscriptionPlan(SubscriptionPlan.ANNUAL));

        List<MemberPlanPriceDto> result = service.getPlansAndPrices();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getSubscriptionPlan() == SubscriptionPlan.MONTHLY));
        assertTrue(result.stream().anyMatch(d -> d.getSubscriptionPlan() == SubscriptionPlan.ANNUAL));
    }

    @Test
    void updatePlanPrices_updatesAndReturnsDto_whenAuthorized() {
        gym.setId(7L);
        when(gymService.findGymEntityByEmail("gym@example.com")).thenReturn(Optional.of(gym));

        MemberPlanPrice existing = new MemberPlanPrice();
        Gym gymRef = new Gym();
        gymRef.setId(7L);
        existing.setGym(gymRef);
        existing.setPrice(BigDecimal.valueOf(10));
        when(memberPricingRepository.findById(100L)).thenReturn(Optional.of(existing));

        when(memberPricingRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        MemberPlanEditDto editDto = new MemberPlanEditDto()
                .setPrice(BigDecimal.valueOf(15))
                .setStudentPrice(BigDecimal.valueOf(12))
                .setSeniorPrice(BigDecimal.valueOf(11))
                .setHandicapPrice(BigDecimal.valueOf(9));

        MemberPlanEditDto mapped = new MemberPlanEditDto()
                .setPrice(editDto.getPrice())
                .setStudentPrice(editDto.getStudentPrice())
                .setSeniorPrice(editDto.getSeniorPrice())
                .setHandicapPrice(editDto.getHandicapPrice());
        when(modelMapper.map(any(MemberPlanPrice.class), eq(MemberPlanEditDto.class))).thenReturn(mapped);

        MemberPlanEditDto result = service.updatePlanPrices(100L, editDto);

        assertEquals(BigDecimal.valueOf(15), result.getPrice());
        verify(memberPricingRepository).save(existing);
    }

    @Test
    void updatePlanPrices_throwsUnauthorized_whenDifferentGym() {
        gym.setId(1L);
        when(gymService.findGymEntityByEmail("gym@example.com")).thenReturn(Optional.of(gym));

        MemberPlanPrice existing = new MemberPlanPrice();
        Gym otherGym = new Gym();
        otherGym.setId(2L);
        existing.setGym(otherGym);
        when(memberPricingRepository.findById(200L)).thenReturn(Optional.of(existing));

        FitManageAppException ex = assertThrows(FitManageAppException.class,
                () -> service.updatePlanPrices(200L, new MemberPlanEditDto().setPrice(BigDecimal.TEN)));

        assertEquals(ApiErrorCode.UNAUTHORIZED, ex.getErrorCode());
        verify(memberPricingRepository, never()).save(any());
    }
}
