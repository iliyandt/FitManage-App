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
    private SubscriptionPlan subscriptionPlan;

    private BigDecimal price;
    private BigDecimal studentPrice;
    private BigDecimal seniorPrice;
    private BigDecimal handicapPrice;

    public MemberPlanPrice() {
    }


    public Gym getGym() {
        return gym;
    }

    public MemberPlanPrice setGym(Gym gym) {
        this.gym = gym;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MemberPlanPrice setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
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
}
