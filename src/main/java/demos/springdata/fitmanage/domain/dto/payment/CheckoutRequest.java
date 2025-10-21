package demos.springdata.fitmanage.domain.dto.payment;


import demos.springdata.fitmanage.domain.enums.Employment;

public class CheckoutRequest {
    private String memberId;
    private String tenantId;
    private String tenantEmail;
    private String plan;
    private Long amount;
    private String currency;
    private Employment employment;
    private String abonnementDuration;


    public CheckoutRequest() {
    }

    public String getMemberId() {
        return memberId;
    }

    public CheckoutRequest setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public CheckoutRequest setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantEmail() {
        return tenantEmail;
    }

    public CheckoutRequest setTenantEmail(String tenantEmail) {
        this.tenantEmail = tenantEmail;
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

    public Employment getEmployment() {
        return employment;
    }

    public CheckoutRequest setEmployment(Employment employment) {
        this.employment = employment;
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
