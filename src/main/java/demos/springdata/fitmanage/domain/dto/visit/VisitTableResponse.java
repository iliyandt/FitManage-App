package demos.springdata.fitmanage.domain.dto.visit;

import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;

public class VisitTableResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private SubscriptionPlan subscriptionPlan;
    private String phone;

    public VisitTableResponse() {
    }

    public Long getId() {
        return id;
    }

    public VisitTableResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public VisitTableResponse setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public VisitTableResponse setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public VisitTableResponse setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public VisitTableResponse setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
