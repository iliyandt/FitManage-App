package demos.springdata.fitmanage.domain.dto.gymmember;

public class GymMemberTableDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String subscriptionPlan;
    private String phone;

    public GymMemberTableDto() {
    }

    public GymMemberTableDto(Long id, String firstName, String lastName, String subscriptionPlan, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.subscriptionPlan = subscriptionPlan;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
