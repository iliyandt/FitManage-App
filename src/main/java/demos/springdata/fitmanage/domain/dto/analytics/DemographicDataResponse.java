package demos.springdata.fitmanage.domain.dto.analytics;
import lombok.*;
import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemographicDataResponse {
    private Map<String, Map<String, Double>> ratios;
}
