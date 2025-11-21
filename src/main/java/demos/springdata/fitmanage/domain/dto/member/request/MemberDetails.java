package demos.springdata.fitmanage.domain.dto.member.request;

import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetails {
    private Employment employment;
    private SubscriptionPlan subscriptionPlan;
}