package demos.springdata.fitmanage.domain.dto.pricing;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;

import java.math.BigDecimal;

public class MemberPlansTableDto {
    private Long id;
    private SubscriptionPlan subscriptionPlan;
    private BigDecimal price;
    private BigDecimal studentPrice;
    private BigDecimal seniorPrice;
    private BigDecimal handicapPrice;


    public MemberPlansTableDto() {
    }

    public Long getId() {
        return id;
    }

    public MemberPlansTableDto setId(Long id) {
        this.id = id;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MemberPlansTableDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MemberPlansTableDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getStudentPrice() {
        return studentPrice;
    }

    public MemberPlansTableDto setStudentPrice(BigDecimal studentPrice) {
        this.studentPrice = studentPrice;
        return this;
    }

    public BigDecimal getSeniorPrice() {
        return seniorPrice;
    }

    public MemberPlansTableDto setSeniorPrice(BigDecimal seniorPrice) {
        this.seniorPrice = seniorPrice;
        return this;
    }

    public BigDecimal getHandicapPrice() {
        return handicapPrice;
    }

    public MemberPlansTableDto setHandicapPrice(BigDecimal handicapPrice) {
        this.handicapPrice = handicapPrice;
        return this;
    }
}
