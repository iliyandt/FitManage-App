package demos.springdata.fitmanage.domain.dto.pricing;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class MemberPlanEditDto {
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal studentPrice;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal seniorPrice;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal handicapPrice;

    public MemberPlanEditDto() {
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MemberPlanEditDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getStudentPrice() {
        return studentPrice;
    }

    public MemberPlanEditDto setStudentPrice(BigDecimal studentPrice) {
        this.studentPrice = studentPrice;
        return this;
    }

    public BigDecimal getSeniorPrice() {
        return seniorPrice;
    }

    public MemberPlanEditDto setSeniorPrice(BigDecimal seniorPrice) {
        this.seniorPrice = seniorPrice;
        return this;
    }

    public BigDecimal getHandicapPrice() {
        return handicapPrice;
    }

    public MemberPlanEditDto setHandicapPrice(BigDecimal handicapPrice) {
        this.handicapPrice = handicapPrice;
        return this;
    }
}
