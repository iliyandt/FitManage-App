package demos.springdata.fitmanage.domain.dto.visit;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VisitTableResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private SubscriptionPlan subscriptionPlan;
    private String phone;
}
