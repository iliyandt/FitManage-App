package demos.springdata.fitmanage.domain.dto.pricing;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class MembershipPlanUpdateDto {
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal studentPrice;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal seniorPrice;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal handicapPrice;

    public MembershipPlanUpdateDto() {
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MembershipPlanUpdateDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getStudentPrice() {
        return studentPrice;
    }

    public MembershipPlanUpdateDto setStudentPrice(BigDecimal studentPrice) {
        this.studentPrice = studentPrice;
        return this;
    }

    public BigDecimal getSeniorPrice() {
        return seniorPrice;
    }

    public MembershipPlanUpdateDto setSeniorPrice(BigDecimal seniorPrice) {
        this.seniorPrice = seniorPrice;
        return this;
    }

    public BigDecimal getHandicapPrice() {
        return handicapPrice;
    }

    public MembershipPlanUpdateDto setHandicapPrice(BigDecimal handicapPrice) {
        this.handicapPrice = handicapPrice;
        return this;
    }
}
