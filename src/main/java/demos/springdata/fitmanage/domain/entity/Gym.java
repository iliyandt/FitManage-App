package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "gyms")
public class Gym extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(unique = true)
    private String phone;
    private String address;

    @Column(nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is required")
    private String password;
    private String city;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "gym_roles",
            joinColumns = @JoinColumn(name = "gym_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    @Column(name = "subscription_valid_until")
    private LocalDate subscriptionValidUntil;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "members_count")
    private int membersCount;

    @OneToMany(mappedBy = "gym")
    private List<GymMember> gymMembers;

    public Gym() {
        this.roles = new HashSet<>();
        this.gymMembers = new ArrayList<>();
    }

    public Gym(String name, String email, String phone, String address, String password, String city, Set<Role> roles, LocalDate subscriptionValidUntil, LocalDateTime createdAt, int membersCount, List<GymMember> gymMembers) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.city = city;
        this.roles = (roles != null) ? roles : new HashSet<>();
        this.subscriptionValidUntil = subscriptionValidUntil;
        this.createdAt = createdAt;
        this.membersCount = membersCount;
        this.gymMembers = (gymMembers != null) ? gymMembers : new ArrayList<>();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.membersCount <= 0) {
            this.membersCount = 0;
        }
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<Role> getRoles() {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = (roles != null) ? roles : new HashSet<>();
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

    public List<GymMember> getGymMembers() {
        return gymMembers;
    }

    public void setGymMembers(List<GymMember> gymMembers) {
        this.gymMembers = gymMembers;
    }



}
