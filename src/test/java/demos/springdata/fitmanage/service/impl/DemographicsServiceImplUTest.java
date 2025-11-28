package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.analytics.DemographicDataResponse;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemographicsServiceImplUTest {

    @Mock
    private UserService userService;

    @Mock
    private MembershipService membershipService;

    @InjectMocks
    private DemographicsServiceImpl demographicsService;

    @Test
    void calculateDemographics_ShouldReturnCorrectPercentages_WhenDataExists() {
        long totalUsers = 10L;
        when(userService.countAllUsersByTenant()).thenReturn(totalUsers);


        when(userService.countByGenderForTenant(any())).thenReturn(0L);
        when(userService.countByGenderForTenant(Gender.MALE)).thenReturn(6L);
        when(userService.countByGenderForTenant(Gender.FEMALE)).thenReturn(4L);


        when(membershipService.countByEmploymentForTenant(any())).thenReturn(0.0); // Default за UNEMPLOYED и др.
        when(membershipService.countByEmploymentForTenant(Employment.STUDENT)).thenReturn(1.0);
        when(membershipService.countByEmploymentForTenant(Employment.SENIOR)).thenReturn(4.0);

        when(membershipService.countBySubscriptionStatusForTenant(any())).thenReturn(0.0); // Default за INACTIVE, CANCELLED
        when(membershipService.countBySubscriptionStatusForTenant(SubscriptionStatus.ACTIVE)).thenReturn(1.0);
        when(membershipService.countBySubscriptionStatusForTenant(SubscriptionStatus.PENDING)).thenReturn(3.0);

        when(membershipService.countBySubscriptionPlanForTenant(any())).thenReturn(0.0); // Default
        when(membershipService.countBySubscriptionPlanForTenant(SubscriptionPlan.MONTHLY)).thenReturn(100.0);

        DemographicDataResponse response = demographicsService.calculateDemographics();

        assertNotNull(response);
        Map<String, Map<String, Double>> data = response.ratios();

        Map<String, Double> genderData = data.get("gender");
        assertEquals(60.0, genderData.get(Gender.MALE.name()));
        assertEquals(40.0, genderData.get(Gender.FEMALE.name()));

        Map<String, Double> employmentData = data.get("employment");
        assertEquals(20.0, employmentData.get(Employment.STUDENT.name()));
        assertEquals(80.0, employmentData.get(Employment.SENIOR.name()));

        assertEquals(0.0, employmentData.get(Employment.REGULAR.name()));

        Map<String, Double> statusData = data.get("subscriptionStatus");
        assertEquals(25.0, statusData.get(SubscriptionStatus.ACTIVE.name()));
        assertEquals(75.0, statusData.get(SubscriptionStatus.PENDING.name()));

        Map<String, Double> planData = data.get("plan");
        assertEquals(100.0, planData.get(SubscriptionPlan.MONTHLY.name()));
        assertEquals(0.0, planData.get(SubscriptionPlan.ANNUAL.name()));
    }
    @Test
    void calculateDemographics_ShouldHandleZeroUsers_Gracefully() {

        when(userService.countAllUsersByTenant()).thenReturn(0L);

        DemographicDataResponse response = demographicsService.calculateDemographics();

        Map<String, Double> genderData = response.ratios().get("gender");

        assertEquals(0.0, genderData.get(Gender.MALE.name()));
        assertEquals(0.0, genderData.get(Gender.FEMALE.name()));


        Map<String, Double> employmentData = response.ratios().get("employment");
        assertEquals(0.0, employmentData.get(Employment.STUDENT.name()));
    }

    @Test
    void calculateDemographics_ShouldRoundCorrectly() {
        long totalUsers = 3L;
        when(userService.countAllUsersByTenant()).thenReturn(totalUsers);

        when(userService.countByGenderForTenant(Gender.MALE)).thenReturn(1L);   // 33.33%
        when(userService.countByGenderForTenant(Gender.FEMALE)).thenReturn(2L); // 66.66%


        DemographicDataResponse response = demographicsService.calculateDemographics();

        Map<String, Double> genderData = response.ratios().get("gender");
        assertEquals(33.0, genderData.get(Gender.MALE.name()));
        assertEquals(67.0, genderData.get(Gender.FEMALE.name()));
    }

    @Test
    void calculateDemographics_ShouldHandleNullReturnsFromService() {

        long totalUsers = 10L;
        when(userService.countAllUsersByTenant()).thenReturn(totalUsers);

        when(membershipService.countByEmploymentForTenant(Employment.STUDENT)).thenReturn(null);
        when(membershipService.countByEmploymentForTenant(Employment.REGULAR)).thenReturn(5.0); // Само 5 валидни

        DemographicDataResponse response = demographicsService.calculateDemographics();

        Map<String, Double> employmentData = response.ratios().get("employment");

        assertEquals(0.0, employmentData.get(Employment.STUDENT.name()));

        assertEquals(100.0, employmentData.get(Employment.REGULAR.name()));
    }
}