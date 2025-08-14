package demos.springdata.fitmanage.domain.dto.pricing;
import demos.springdata.fitmanage.annotation.DropDown;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;

public class MemberPlanPriceDto {

    private Long id;

    @DropDown(url = "/v1/subscription_plans/customized_fields")
    private SubscriptionPlan subscriptionPlan;

    public MemberPlanPriceDto() {
    }

    public Long getId() {
        return id;
    }

    public MemberPlanPriceDto setId(Long id) {
        this.id = id;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MemberPlanPriceDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }
}