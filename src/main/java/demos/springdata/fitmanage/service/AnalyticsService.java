package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.analytics.UserRatioAnalyticsDto;

public interface AnalyticsService {
    UserRatioAnalyticsDto calculateUserRatios();
}
