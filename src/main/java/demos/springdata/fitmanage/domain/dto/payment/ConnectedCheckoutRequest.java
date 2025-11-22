package demos.springdata.fitmanage.domain.dto.payment;

import demos.springdata.fitmanage.domain.enums.Employment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectedCheckoutRequest {
    private Long userId;
    private String subscriptionPlan;
    private Long amount;
    private Integer allowedVisits;
    private String currency;
    private Employment employment;
}
