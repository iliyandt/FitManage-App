package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.analytics.DemographicDataResponse;

public interface DemographicsService {
    DemographicDataResponse calculateDemographics();
}
