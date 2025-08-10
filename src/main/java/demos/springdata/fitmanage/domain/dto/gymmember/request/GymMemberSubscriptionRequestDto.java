package demos.springdata.fitmanage.domain.dto.gymmember.request;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

public class GymMemberSubscriptionRequestDto {
    private Integer visitLimit;
    private SubscriptionStatus subscriptionStatus;
    private SubscriptionPlan subscriptionPlan;


    public GymMemberSubscriptionRequestDto() {
    }


    public Integer getVisitLimit() {
        return visitLimit;
    }

    public GymMemberSubscriptionRequestDto setVisitLimit(Integer visitLimit) {
        this.visitLimit = visitLimit;
        return this;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public GymMemberSubscriptionRequestDto setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public GymMemberSubscriptionRequestDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }
}
