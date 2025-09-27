package demos.springdata.fitmanage.domain.dto.analytics;

import java.util.Map;

public class UserRatioAnalyticsDto {

    private Map<String, Map<String, Double>> ratios;

    public UserRatioAnalyticsDto() {
    }

    public Map<String, Map<String, Double>> getRatios() {
        return ratios;
    }

    public UserRatioAnalyticsDto setRatios(Map<String, Map<String, Double>> ratios) {
        this.ratios = ratios;
        return this;
    }
}
