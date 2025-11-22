package demos.springdata.fitmanage.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    private String tenantId;
    private String businessEmail;
    private String plan;
    private Long amount;
    private String currency;
    private String abonnementDuration;
}
