package demos.springdata.fitmanage.domain.dto.visit;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VisitTableResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private SubscriptionPlan subscriptionPlan;
    private String phone;
}
