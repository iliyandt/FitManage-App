package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.analytics.UserRatioAnalyticsDto;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.service.AnalyticsService;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
        Long totalUsers = userService.countAllUsersByTenant();

        Map<String, Map<String, Double>> analytics = new HashMap<>();

        Map<String, Double> gendersCount = new HashMap<>();
        if (totalUsers == 0) {
            Arrays.stream(Gender.values()).forEach(gender -> gendersCount.put(gender.name(), 0.0));
        } else {
            Arrays.stream(Gender.values()).forEach(gender -> {
                double percentage = userService.countByGenderForTenant(gender) * 100.0 / totalUsers;
                double rounded = Math.round(percentage);
                gendersCount.put(gender.name(), rounded);
            });
        }

        Map<String, Double> employmentCount = calculatePercentages(
                Employment.values(),
                membershipService::countByEmploymentForTenant
        );

        Map<String, Double> subscriptionStatus = calculatePercentages(
                SubscriptionStatus.values(),
                membershipService::countBySubscriptionStatusForTenant
        );

        Map<String, Double> subscriptionPlan = calculatePercentages(
                SubscriptionPlan.values(),
                membershipService::countBySubscriptionPlanForTenant
        );

        analytics.put("gender", gendersCount);
        analytics.put("employment", employmentCount);
        analytics.put("subscriptionStatus", subscriptionStatus);
        analytics.put("plan", subscriptionPlan);

        return new UserRatioAnalyticsDto().setRatios(analytics);
    }


    private <T extends Enum<T>> Map<String, Double> calculatePercentages(T[] values, Function<T, Double> counter) {
        Map<String, Double> result = new HashMap<>();

        double total = Arrays.stream(values)
                .mapToDouble(value -> {
                    Double count = counter.apply(value);
                    return count != null ? count : 0.0;
                })
                .sum();

        if (total == 0.0) {
            for (T value : values) {
                result.put(value.name(), 0.0);
            }
        } else {
            for (T value : values) {
                Double count = counter.apply(value);
                double safeCount = count != null ? count : 0.0;
                double percentage = safeCount * 100.0 / total;

                double rounded = Math.round(percentage);
                result.put(value.name(), rounded);
            }
        }

        return result;
    }

}
