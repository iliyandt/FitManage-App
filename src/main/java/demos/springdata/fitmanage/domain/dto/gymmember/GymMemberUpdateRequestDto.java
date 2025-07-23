package demos.springdata.fitmanage.domain.dto.gymmember;

import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import jakarta.validation.constraints.Size;

public class GymMemberUpdateRequestDto {
    @Size
    private String firstName;
    private String lastName;
    private String email;
    private SubscriptionStatus subscriptionStatus;
    private String phone;

    public GymMemberUpdateRequestDto() {
    }

    public GymMemberUpdateRequestDto(String firstName, String lastName, String email, SubscriptionStatus subscriptionStatus, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.subscriptionStatus = subscriptionStatus;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
