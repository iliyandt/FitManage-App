package demos.springdata.fitmanage.domain.dto.pricing;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MemberPlanPriceDto {
    private Long id;
    private SubscriptionPlan subscriptionPlan;
    @NotNull(message = "Price is required")
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
