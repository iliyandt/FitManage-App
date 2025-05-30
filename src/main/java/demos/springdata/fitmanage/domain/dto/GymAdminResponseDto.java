package demos.springdata.fitmanage.domain.dto;

import demos.springdata.fitmanage.domain.entity.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class GymAdminResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private Set<RoleDto> roles = new HashSet<>();
    private LocalDate subscriptionValidUntil;
    private LocalDateTime createdAt;
    private int membersCount;
    private boolean isSubscriptionActive;


    public GymAdminResponseDto() {
    }

    public GymAdminResponseDto(Long id, String name, String email, String phone, String address, String city, Set<RoleDto> roles, LocalDate subscriptionValidUntil, LocalDateTime createdAt, int membersCount, boolean isSubscriptionActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.roles = roles;
        this.subscriptionValidUntil = subscriptionValidUntil;
        this.createdAt = createdAt;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }

    public LocalDate getSubscriptionValidUntil() {
        return subscriptionValidUntil;
    }

    public void setSubscriptionValidUntil(LocalDate subscriptionValidUntil) {
        this.subscriptionValidUntil = subscriptionValidUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
