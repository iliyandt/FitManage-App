package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "gym_members")
public class GymMember extends BaseEntity implements UserDetails {

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Employment employment;
    @Column(name = "birth_date")
    private OffsetDateTime birthDate;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String phone;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "member_roles",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    private boolean enabled;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;
    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private Integer allowedVisits;
    private Integer remainingVisits;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    public GymMember() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.enabled = true;
    }

    public String getFirstName() {
        return firstName;
    }

    public GymMember setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public GymMember setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public GymMember setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public GymMember setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public GymMember setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public GymMember setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public GymMember setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public GymMember setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public GymMember setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public GymMember setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public GymMember setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Gym getGym() {
        return gym;
    }

    public GymMember setGym(Gym gym) {
        this.gym = gym;
        return this;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public GymMember setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public GymMember setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public LocalDateTime getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public GymMember setSubscriptionStartDate(LocalDateTime subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
        return this;
    }

    public LocalDateTime getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public GymMember setSubscriptionEndDate(LocalDateTime subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
        return this;
    }

    public Integer getAllowedVisits() {
        return allowedVisits;
    }

    public GymMember setAllowedVisits(Integer totalVisits) {
        this.allowedVisits = totalVisits;
        return this;
    }

    public Integer getRemainingVisits() {
        return remainingVisits;
    }

    public GymMember setRemainingVisits(Integer remainingVisits) {
        this.remainingVisits = remainingVisits;
        return this;
    }
}
