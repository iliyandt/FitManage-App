package demos.springdata.fitmanage.domain.dto.member.request;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;

public class MemberSubscriptionRequestDto {
    private Integer allowedVisits;
    private SubscriptionPlan subscriptionPlan;
    private Employment employment;

    public MemberSubscriptionRequestDto() {
    }


    public Integer getAllowedVisits() {
        return allowedVisits;
    }

    public MemberSubscriptionRequestDto setAllowedVisits(Integer allowedVisits) {
        this.allowedVisits = allowedVisits;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MemberSubscriptionRequestDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public MemberSubscriptionRequestDto setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }
}
