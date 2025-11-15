package demos.springdata.fitmanage.domain.dto.analytics;
import java.util.Map;

public record DemographicDataResponse(Map<String, Map<String, Double>> ratios) {
    public DemographicDataResponse(Map<String, Map<String, Double>> ratios) {
        this.ratios = ratios;
    }
}
