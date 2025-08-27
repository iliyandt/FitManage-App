package demos.springdata.fitmanage.domain.dto.membershipplan;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

import java.time.LocalDate;

public class MembershipPlanDto {

    private Long id;
    private SubscriptionPlan subscriptionPlan;
    private SubscriptionStatus subscriptionStatus;
    private LocalDate subscriptionStartDate;
    private LocalDate subscriptionEndDate;
    private Integer allowedVisits;
    private Integer remainingVisits;

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

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public MembershipPlanDto setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
        return this;
    }

    public LocalDate getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public MembershipPlanDto setSubscriptionStartDate(LocalDate subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
        return this;
    }

    public LocalDate getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public MembershipPlanDto setSubscriptionEndDate(LocalDate subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
        return this;
    }

    public Integer getAllowedVisits() {
        return allowedVisits;
    }

    public MembershipPlanDto setAllowedVisits(Integer allowedVisits) {
        this.allowedVisits = allowedVisits;
        return this;
    }

    public Integer getRemainingVisits() {
        return remainingVisits;
    }

    public MembershipPlanDto setRemainingVisits(Integer remainingVisits) {
        this.remainingVisits = remainingVisits;
        return this;
    }
}