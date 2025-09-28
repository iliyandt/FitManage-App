package demos.springdata.fitmanage.domain.dto.payment;

public class CheckoutRequest {
    private Long amount;
    private String currency;

    public CheckoutRequest() {
    }

    public Long getAmount() {
        return amount;
    }

    public CheckoutRequest setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public CheckoutRequest setCurrency(String currency) {
        this.currency = currency;
        return this;
    }
}
