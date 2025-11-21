package demos.springdata.fitmanage.domain.dto.membershipplan;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanDto {
    private Long id;
    private SubscriptionPlan subscriptionPlan;
    private BigDecimal price;
    private BigDecimal studentPrice;
    private BigDecimal seniorPrice;
    private BigDecimal handicapPrice;
}