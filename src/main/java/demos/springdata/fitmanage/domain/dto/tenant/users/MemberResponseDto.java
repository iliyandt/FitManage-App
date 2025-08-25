package demos.springdata.fitmanage.domain.dto.tenant.users;

import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

import java.time.LocalDateTime;

public final class MemberResponseDto extends UserBaseResponseDto {
    private SubscriptionPlan subscriptionPlan;
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private Integer allowedVisits;
    private Integer remainingVisits;
    private LocalDateTime lastCheckInAt;
    private Employment employment;

    public MemberResponseDto() {
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MemberResponseDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public MemberResponseDto setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
        return this;
    }

    public LocalDateTime getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public MemberResponseDto setSubscriptionStartDate(LocalDateTime subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
        return this;
    }

    public LocalDateTime getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public MemberResponseDto setSubscriptionEndDate(LocalDateTime subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
        return this;
    }

    public Integer getAllowedVisits() {
        return allowedVisits;
    }

    public MemberResponseDto setAllowedVisits(Integer allowedVisits) {
        this.allowedVisits = allowedVisits;
        return this;
    }

    public Integer getRemainingVisits() {
        return remainingVisits;
    }

    public MemberResponseDto setRemainingVisits(Integer remainingVisits) {
        this.remainingVisits = remainingVisits;
        return this;
    }

    public LocalDateTime getLastCheckInAt() {
        return lastCheckInAt;
    }

    public MemberResponseDto setLastCheckInAt(LocalDateTime lastCheckInAt) {
        this.lastCheckInAt = lastCheckInAt;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public MemberResponseDto setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }
}
