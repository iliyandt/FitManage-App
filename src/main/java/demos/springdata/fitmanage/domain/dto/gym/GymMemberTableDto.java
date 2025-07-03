package demos.springdata.fitmanage.domain.dto.gym;

public class GymMemberTableDto {

    private String firstName;
    private String lastName;
    private String subscriptionPlan;
    private String phone;

    public GymMemberTableDto() {
    }

    public GymMemberTableDto(String firstName, String lastName, String subscriptionPlan, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.subscriptionPlan = subscriptionPlan;
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

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
