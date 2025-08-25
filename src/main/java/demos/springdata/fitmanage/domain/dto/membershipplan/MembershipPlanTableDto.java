package demos.springdata.fitmanage.domain.dto.membershipplan;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;

import java.math.BigDecimal;

public class MembershipPlanTableDto {
    private Long id;
    private SubscriptionPlan subscriptionPlan;
    private BigDecimal price;
    private BigDecimal studentPrice;
    private BigDecimal seniorPrice;
    private BigDecimal handicapPrice;


    public MembershipPlanTableDto() {
    }

    public Long getId() {
        return id;
    }

    public MembershipPlanTableDto setId(Long id) {
        this.id = id;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MembershipPlanTableDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MembershipPlanTableDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getStudentPrice() {
        return studentPrice;
    }

    public MembershipPlanTableDto setStudentPrice(BigDecimal studentPrice) {
        this.studentPrice = studentPrice;
        return this;
    }

    public BigDecimal getSeniorPrice() {
        return seniorPrice;
    }

    public MembershipPlanTableDto setSeniorPrice(BigDecimal seniorPrice) {
        this.seniorPrice = seniorPrice;
        return this;
    }

    public BigDecimal getHandicapPrice() {
        return handicapPrice;
    }

    public MembershipPlanTableDto setHandicapPrice(BigDecimal handicapPrice) {
        this.handicapPrice = handicapPrice;
        return this;
    }
}
