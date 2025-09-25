package demos.springdata.fitmanage.domain.dto.membershipplan;

import java.math.BigDecimal;

public class PlanPriceResponse {
    private BigDecimal price;

    public PlanPriceResponse() {
    }

    public BigDecimal getPrice() {
        return price;
    }

    public PlanPriceResponse setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
}
