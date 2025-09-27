package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.analytics.UserRatioAnalyticsDto;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.service.AnalyticsService;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    private final UserService userService;
    private final MembershipService membershipService;

    @Autowired
    public AnalyticsServiceImpl(UserService userService, MembershipService membershipService) {
        this.userService = userService;
        this.membershipService = membershipService;
    }

    @Override
    public UserRatioAnalyticsDto calculateUserRatios() {
        Double totalUsers = userService.countAllUsersByTenant();

        Map<String, Map<String, Double>> analytics = new HashMap<>();
        Map<String, Double> gendersCount = new HashMap<>();
        Map<String, Double> employmentCount = new HashMap<>();
        Map<String, Double> subscriptionStatus = new HashMap<>();
        Map<String, Double> subscriptionPlan = new HashMap<>();

        //GENDER
        Arrays.stream(Gender.values()).forEach(gender -> {
            gendersCount.put(
                    gender.name(),
                    Double.valueOf(String.format("%.2f", userService.countByGenderForTenant(gender) * 100.0 / totalUsers)));
        });


        //EMPLOYMENT
        double totalWithEmployment = Arrays.stream(Employment.values())
                .mapToDouble(e -> {
                    Double count = membershipService.countByEmploymentForTenant(e);
                    return count != null ? count : 0.0;
                })
                .sum();

        Arrays.stream(Employment.values())
                .forEach(employment -> {
                employmentCount.put(
                        employment.name(),
                        Double.valueOf(String.format("%.2f", membershipService.countByEmploymentForTenant(employment) * 100.0 / totalWithEmployment)));
        });


        //SUBSCRIPTION_STATUS
        double totalWithStatus = Arrays.stream(SubscriptionStatus.values())
                .mapToDouble(s -> {
                    Double count = membershipService.countBySubscriptionStatusForTenant(s);
                    return count != null ? count : 0.0;
                })
                .sum();

        Arrays.stream(SubscriptionStatus.values())
                .forEach(status -> {
                    subscriptionStatus.put(
                            status.name(),
                            Double.valueOf(String.format("%.2f", membershipService.countBySubscriptionStatusForTenant(status) * 100.0 / totalWithStatus)));
                });

        //SUBSCRIPTION_PLAN
        double totalWithPlan = Arrays.stream(SubscriptionPlan.values())
                .mapToDouble(plan -> {
                    Double count = membershipService.countBySubscriptionPlanForTenant(plan);
                    return count != null ? count : 0.0;
                })
                .sum();

        Arrays.stream(SubscriptionPlan.values())
                .forEach(plan -> {
                    subscriptionPlan.put(
                            plan.name(),
                            Double.valueOf(String.format("%.2f", membershipService.countBySubscriptionPlanForTenant(plan) * 100.0 / totalWithPlan)));
                });



        analytics.put("gender", gendersCount);
        analytics.put("employment", employmentCount);
        analytics.put("status", subscriptionStatus);
        analytics.put("plan", subscriptionPlan);

        return new UserRatioAnalyticsDto().setRatios(analytics);
    }
}
