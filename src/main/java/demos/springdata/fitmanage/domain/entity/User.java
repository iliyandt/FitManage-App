package demos.springdata.fitmanage.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_email_tenant", columnNames = {"email", "tenant_id"}),
                @UniqueConstraint(name = "uq_user_phone_tenant", columnNames = {"phone", "tenant_id"}),
                @UniqueConstraint(name = "uq_user_username_tenant", columnNames = {"username", "tenant_id"})
        })
public class User extends BaseEntity {
    @Column(name = "qr_token", unique = true)
    private String qrToken;

    private String firstName;
    private String lastName;
    private String username;
    private String email;

    @Column(nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private AccountSettings accountSettings;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    private String phone;
    private String address;
    private String city;
    private boolean enabled;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "birth_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime birthDate;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private Set<Membership> memberships = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Employee> employees = new ArrayList<>();

    @ManyToMany(mappedBy = "recipients")
    private Set<News> targetedNews = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<News> authoredNews = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public User() {
    }

    public String getQrToken() {
        return qrToken;
    }

    public User setQrToken(String qrToken) {
        this.qrToken = qrToken;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public User setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }

    public LocalDateTime getVerificationCodeExpiresAt() {
        return verificationCodeExpiresAt;
    }

    public User setVerificationCodeExpiresAt(LocalDateTime verificationCodeExpiresAt) {
        this.verificationCodeExpiresAt = verificationCodeExpiresAt;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public User setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public User setTenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public AccountSettings getAccountSettings() {
        return accountSettings;
    }

    public User setAccountSettings(AccountSettings accountSettings) {
        this.accountSettings = accountSettings;
        return this;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public User setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public User setCity(String city) {
        this.city = city;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public User setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public User setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Set<Membership> getMemberships() {
        return memberships;
    }

    public User setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
        return this;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public User setEmployees(List<Employee> employees) {
        this.employees = employees;
        return this;
    }

    public Set<News> getTargetedNews() {
        return targetedNews;
    }

    public User setTargetedNews(Set<News> targetedNews) {
        this.targetedNews = targetedNews;
        return this;
    }

    public Set<News> getAuthoredNews() {
        return authoredNews;
    }

    public User setAuthoredNews(Set<News> authoredNews) {
        this.authoredNews = authoredNews;
        return this;
    }
}
