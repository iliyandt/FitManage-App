package demos.springdata.fitmanage.domain.dto.pricing;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;

import java.math.BigDecimal;

public class MemberPlanPriceDto {
    private SubscriptionPlan planType;
    private String customPlanName;
    private BigDecimal price;
    private String currency;
    private BigDecimal discountPercentage;


    public MemberPlanPriceDto() {
    }


    public SubscriptionPlan getPlanType() {
        return planType;
    }

    public MemberPlanPriceDto setPlanType(SubscriptionPlan planType) {
        this.planType = planType;
        return this;
    }

    public String getCustomPlanName() {
        return customPlanName;
    }

    public MemberPlanPriceDto setCustomPlanName(String customPlanName) {
        this.customPlanName = customPlanName;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MemberPlanPriceDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public MemberPlanPriceDto setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public MemberPlanPriceDto setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        return this;
    }
}
