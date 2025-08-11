package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "member_plan_prices")
public class MemberPlanPrice extends BaseEntity{
    @ManyToOne(optional = false)
    private Gym gym;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan planType;

    @Column(name = "custom_plan_name")
    private String customPlanName;

    @Column(nullable = false)
    private BigDecimal price;
    private BigDecimal studentPrice;
    private BigDecimal seniorPrice;
    private BigDecimal handicapPrice;

    @Column(nullable = false)
    private String currency;


    public MemberPlanPrice() {
    }

    public boolean isCustomPlan() {
        return planType == null && customPlanName != null;
    }

    public Gym getGym() {
        return gym;
    }

    public MemberPlanPrice setGym(Gym gym) {
        this.gym = gym;
        return this;
    }

    public SubscriptionPlan getPlanType() {
        return planType;
    }

    public MemberPlanPrice setPlanType(SubscriptionPlan planType) {
        this.planType = planType;
        return this;
    }

    public String getCustomPlanName() {
        return customPlanName;
    }

    public MemberPlanPrice setCustomPlanName(String customPlanName) {
        this.customPlanName = customPlanName;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MemberPlanPrice setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getStudentPrice() {
        return studentPrice;
    }

    public MemberPlanPrice setStudentPrice(BigDecimal studentPrice) {
        this.studentPrice = studentPrice;
        return this;
    }

    public BigDecimal getSeniorPrice() {
        return seniorPrice;
    }

    public MemberPlanPrice setSeniorPrice(BigDecimal seniorPrice) {
        this.seniorPrice = seniorPrice;
        return this;
    }

    public BigDecimal getHandicapPrice() {
        return handicapPrice;
    }

    public MemberPlanPrice setHandicapPrice(BigDecimal handicapPrice) {
        this.handicapPrice = handicapPrice;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public MemberPlanPrice setCurrency(String currency) {
        this.currency = currency;
        return this;
    }
}
