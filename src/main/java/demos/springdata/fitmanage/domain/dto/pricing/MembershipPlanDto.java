package demos.springdata.fitmanage.domain.dto.pricing;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;

public class MembershipPlanDto {

    private Long id;
    private SubscriptionPlan subscriptionPlan;

    public MembershipPlanDto() {
    }

    public Long getId() {
        return id;
    }

    public MembershipPlanDto setId(Long id) {
        this.id = id;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MembershipPlanDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }
}