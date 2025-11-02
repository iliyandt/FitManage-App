package demos.springdata.fitmanage.domain.dto.payment;

public class CheckoutRequest {
    private String tenantId;
    private String plan;
    private Long amount;
    private String currency;
    private String abonnementDuration;


    public CheckoutRequest() {
    }


    public String getTenantId() {
        return tenantId;
    }

    public CheckoutRequest setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getPlan() {
        return plan;
    }

    public CheckoutRequest setPlan(String plan) {
        this.plan = plan;
        return this;
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

    public String getAbonnementDuration() {
        return abonnementDuration;
    }

    public CheckoutRequest setAbonnementDuration(String abonnementDuration) {
        this.abonnementDuration = abonnementDuration;
        return this;
    }
}
