package demos.springdata.fitmanage.domain.dto.pricing;

import com.fasterxml.jackson.annotation.JsonGetter;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class MemberPlanPriceDto {

    private Long id;
    private SubscriptionPlan subscriptionPlan;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal studentPrice;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal seniorPrice;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal handicapPrice;

    @NotBlank(message = "Currency is required")
    private String currency;

    public MemberPlanPriceDto() {
    }

    public Long getId() {
        return id;
    }

    public MemberPlanPriceDto setId(Long id) {
        this.id = id;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MemberPlanPriceDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    @JsonGetter("subscriptionPlanDisplayName")
    public String getSubscriptionPlanDisplayName() {
        return subscriptionPlan != null ? subscriptionPlan.getDisplayName() : null;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MemberPlanPriceDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getStudentPrice() {
        return studentPrice;
    }

    public MemberPlanPriceDto setStudentPrice(BigDecimal studentPrice) {
        this.studentPrice = studentPrice;
        return this;
    }

    public BigDecimal getSeniorPrice() {
        return seniorPrice;
    }

    public MemberPlanPriceDto setSeniorPrice(BigDecimal seniorPrice) {
        this.seniorPrice = seniorPrice;
        return this;
    }

    public BigDecimal getHandicapPrice() {
        return handicapPrice;
    }

    public MemberPlanPriceDto setHandicapPrice(BigDecimal handicapPrice) {
        this.handicapPrice = handicapPrice;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public MemberPlanPriceDto setCurrency(String currency) {
        this.currency = currency;
        return this;
    }
}
