package demos.springdata.fitmanage.domain.dto.gym;

public class GymSummaryDto {
    private Long id;
    private String username;
    private String email;
    private String city;
    private String phone;
    private int membersCount;
    private boolean isSubscriptionActive;


    public GymSummaryDto() {
    }

    public GymSummaryDto(Long id, String username, String city, String email, String phone, int membersCount, boolean isSubscriptionActive) {
        this.id = id;
        this.username = username;
        this.city = city;
        this.email = email;
        this.phone = phone;
        this.membersCount = membersCount;
        this.isSubscriptionActive = isSubscriptionActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    public boolean isSubscriptionActive() {
        return isSubscriptionActive;
    }

    public void setSubscriptionActive(boolean subscriptionActive) {
        isSubscriptionActive = subscriptionActive;
    }


}
