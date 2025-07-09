package demos.springdata.fitmanage.domain.dto.gymmember;

import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

public class GymMemberTableDto {

    private Long id;
    private String fullName;
    private SubscriptionStatus subscriptionStatus; //subscriptionStatus
    private String phone;

    public GymMemberTableDto() {
    }

    public GymMemberTableDto(Long id, String fullName, SubscriptionStatus subscriptionStatus, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.subscriptionStatus = subscriptionStatus;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
