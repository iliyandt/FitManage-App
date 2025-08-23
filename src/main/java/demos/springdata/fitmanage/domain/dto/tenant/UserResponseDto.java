package demos.springdata.fitmanage.domain.dto.tenant;

import demos.springdata.fitmanage.domain.entity.Role;

import java.util.Set;

public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String city;
    private String address;
    private String phone;
    private int membersCount;
    private boolean isSubscriptionActive;
    private Set<Role> role;


    public UserResponseDto() {
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

    public String getAddress() {
        return address;
    }

    public UserResponseDto setAddress(String address) {
        this.address = address;
        return this;
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

    public Set<Role> getRole() {
        return role;
    }

    public UserResponseDto setRole(Set<Role> role) {
        this.role = role;
        return this;
    }
}
