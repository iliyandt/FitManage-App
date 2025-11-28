package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.membershipplan.PlanDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.PriceResponse;
import demos.springdata.fitmanage.domain.dto.membershipplan.UpdateRequest;
import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.MembershipPlanRepository;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MembershipPlanServiceImplUTest {

    @Mock
    private MembershipPlanRepository membershipPlanRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MembershipPlanServiceImpl membershipPlanService;

    @Captor
    private ArgumentCaptor<MembershipPlan> planCaptor;

    // --- CREATE TESTS ---

    @Test
    void createPlans_ShouldSaveAndReturnPlans() {
        // Arrange
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        User admin = new User();
        admin.setTenant(tenant);

        when(userService.getCurrentUser()).thenReturn(admin);

        PlanDto dto1 = PlanDto.builder()
                .subscriptionPlan(SubscriptionPlan.MONTHLY)
                .price(BigDecimal.valueOf(100))
                .build();

        PlanDto dto2 = PlanDto.builder()
                .subscriptionPlan(SubscriptionPlan.ANNUAL)
                .price(BigDecimal.valueOf(200))
                .build();

        List<PlanDto> inputList = List.of(dto1, dto2);

        List<PlanDto> result = membershipPlanService.createPlans(inputList);

        assertNotNull(result);
        assertEquals(2, result.size());

        // Проверяваме дали repository.save е извикан 2 пъти
        verify(membershipPlanRepository, times(2)).save(planCaptor.capture());

        List<MembershipPlan> savedPlans = planCaptor.getAllValues();

        // Проверяваме дали Tenant-а е сетнат правилно на всички планове
        assertTrue(savedPlans.stream().allMatch(p -> p.getTenant().equals(tenant)));

        // Проверяваме мапването на първия план
        assertEquals(SubscriptionPlan.MONTHLY, savedPlans.get(0).getSubscriptionPlan());
        assertEquals(BigDecimal.valueOf(100), savedPlans.get(0).getPrice());
    }

    // --- GET DATA TESTS ---

    @Test
    void getPlansData_ShouldReturnPlansForCurrentTenant() {
        // Arrange
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        User admin = new User();
        admin.setTenant(tenant);

        MembershipPlan planEntity = new MembershipPlan();
        planEntity.setId(UUID.randomUUID());
        planEntity.setSubscriptionPlan(SubscriptionPlan.MONTHLY);
        planEntity.setPrice(BigDecimal.valueOf(150));

        when(userService.getCurrentUser()).thenReturn(admin);
        when(membershipPlanRepository.getMembershipPlansByTenant(tenant)).thenReturn(List.of(planEntity));

        // Act
        List<PlanDto> result = membershipPlanService.getPlansData();

        // Assert
        assertEquals(1, result.size());
        assertEquals(planEntity.getId(), result.get(0).getId());
        assertEquals(SubscriptionPlan.MONTHLY, result.get(0).getSubscriptionPlan());
    }

    // --- UPDATE TESTS ---

    @Test
    void updatePlanPrices_ShouldUpdateAllPriceFields() {
        // Arrange
        UUID planId = UUID.randomUUID();
        MembershipPlan existingPlan = new MembershipPlan();
        existingPlan.setId(planId);
        existingPlan.setPrice(BigDecimal.TEN); // Стара цена

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setPrice(BigDecimal.valueOf(50));
        updateRequest.setStudentPrice(BigDecimal.valueOf(40));
        updateRequest.setSeniorPrice(BigDecimal.valueOf(30));
        updateRequest.setHandicapPrice(BigDecimal.valueOf(20));

        // В кода ти се използва custom метод getMembershipPlanById, който връща директно обекта
        when(membershipPlanRepository.getMembershipPlanById(planId)).thenReturn(existingPlan);

        // Act
        membershipPlanService.updatePlanPrices(planId, updateRequest);

        // Assert
        verify(membershipPlanRepository).save(planCaptor.capture());
        MembershipPlan updatedPlan = planCaptor.getValue();

        assertEquals(BigDecimal.valueOf(50), updatedPlan.getPrice());
        assertEquals(BigDecimal.valueOf(40), updatedPlan.getStudentPrice());
        assertEquals(BigDecimal.valueOf(30), updatedPlan.getSeniorPrice());
        assertEquals(BigDecimal.valueOf(20), updatedPlan.getHandicapPrice());
    }

    // --- DELETE TESTS ---

    @Test
    void deletePlan_ShouldDelete_WhenFound() {
        // Arrange
        UUID planId = UUID.randomUUID();
        MembershipPlan plan = new MembershipPlan();

        when(membershipPlanRepository.findById(planId)).thenReturn(Optional.of(plan));

        // Act
        membershipPlanService.deletePlan(planId);

        // Assert
        verify(membershipPlanRepository).delete(plan);
    }

    @Test
    void deletePlan_ShouldThrowException_WhenNotFound() {
        // Arrange
        UUID planId = UUID.randomUUID();
        when(membershipPlanRepository.findById(planId)).thenReturn(Optional.empty());

        // Act & Assert
        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                membershipPlanService.deletePlan(planId)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getErrorCode());
        verify(membershipPlanRepository, never()).delete(any());
    }

    // --- GET PRICE SPECIFIC ---

    @Test
    void getPlanPrice_ShouldReturnCorrectPrice() {
        // Arrange
        Tenant tenant = new Tenant();
        User admin = new User();
        admin.setTenant(tenant);

        BigDecimal expectedPrice = BigDecimal.valueOf(99.99);

        when(userService.getCurrentUser()).thenReturn(admin);
        when(membershipPlanRepository.findPriceByTenantAndSubscriptionPlanAndEmployment(
                tenant, SubscriptionPlan.MONTHLY, Employment.REGULAR.name()
        )).thenReturn(expectedPrice);

        // Act
        PriceResponse response = membershipPlanService.getPlanPrice(SubscriptionPlan.MONTHLY, Employment.REGULAR);

        // Assert
        assertNotNull(response);
        assertEquals(expectedPrice, response.price());
    }
}