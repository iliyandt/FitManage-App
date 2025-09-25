package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "membership_plans")
public class MembershipPlan extends BaseEntity{
    @ManyToOne(optional = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan;

    private BigDecimal price;
    private BigDecimal studentPrice;
    private BigDecimal seniorPrice;
    private BigDecimal handicapPrice;

    public MembershipPlan() {
    }

    public Tenant getTenant() {
        return tenant;
    }

    public MembershipPlan setTenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MembershipPlan setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MembershipPlan setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getStudentPrice() {
        return studentPrice;
    }

    public MembershipPlan setStudentPrice(BigDecimal studentPrice) {
        this.studentPrice = studentPrice;
        return this;
    }

    public BigDecimal getSeniorPrice() {
        return seniorPrice;
    }

    public MembershipPlan setSeniorPrice(BigDecimal seniorPrice) {
        this.seniorPrice = seniorPrice;
        return this;
    }

    public BigDecimal getHandicapPrice() {
        return handicapPrice;
    }

    public MembershipPlan setHandicapPrice(BigDecimal handicapPrice) {
        this.handicapPrice = handicapPrice;
        return this;
    }
}
