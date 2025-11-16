package demos.springdata.fitmanage.domain.dto.member.request;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;


public record SubscriptionRequest(
        Integer allowedVisits,
        SubscriptionPlan subscriptionPlan,
        Employment employment
) {
}
