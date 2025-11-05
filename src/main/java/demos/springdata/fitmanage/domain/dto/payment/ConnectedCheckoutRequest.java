package demos.springdata.fitmanage.domain.dto.payment;

import demos.springdata.fitmanage.domain.enums.Employment;


public class ConnectedCheckoutRequest {

    private Long userId;
    private String subscriptionPlan;
    private Long amount;
    private Integer allowedVisits;
    private String currency;
    private Employment employment;

    public ConnectedCheckoutRequest() {
    }


    public Long getUserId() {
        return userId;
    }

    public ConnectedCheckoutRequest setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public ConnectedCheckoutRequest setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public Long getAmount() {
        return amount;
    }

    public ConnectedCheckoutRequest setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public Integer getAllowedVisits() {
        return allowedVisits;
    }

    public ConnectedCheckoutRequest setAllowedVisits(Integer allowedVisits) {
        this.allowedVisits = allowedVisits;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public ConnectedCheckoutRequest setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public ConnectedCheckoutRequest setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }
}
