package demos.springdata.fitmanage.domain.dto.gym;

public class GymSummaryDto {
    private Long id;
    private String name;
    private String email;
    private String city;
    private int membersCount;
    private boolean isSubscriptionActive;


    public GymSummaryDto() {
    }

    public GymSummaryDto(Long id, String name, String city, String email, int membersCount, boolean isSubscriptionActive) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.email = email;
        this.membersCount = membersCount;
        this.isSubscriptionActive = isSubscriptionActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
