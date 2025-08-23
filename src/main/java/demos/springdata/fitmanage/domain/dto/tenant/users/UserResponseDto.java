package demos.springdata.fitmanage.domain.dto.tenant.users;

import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Employment employment;
    private OffsetDateTime birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String phone;
    private SubscriptionPlan subscriptionPlan;
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private Integer allowedVisits;
    private Integer remainingVisits;
    private LocalDateTime lastCheckInAt;

    public UserResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public UserResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserResponseDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserResponseDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserResponseDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public UserResponseDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public UserResponseDto setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public UserResponseDto setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UserResponseDto setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UserResponseDto setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserResponseDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public UserResponseDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public UserResponseDto setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
        return this;
    }

    public LocalDateTime getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public UserResponseDto setSubscriptionStartDate(LocalDateTime subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
        return this;
    }

    public LocalDateTime getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public UserResponseDto setSubscriptionEndDate(LocalDateTime subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
        return this;
    }

    public Integer getAllowedVisits() {
        return allowedVisits;
    }

    public UserResponseDto setAllowedVisits(Integer allowedVisits) {
        this.allowedVisits = allowedVisits;
        return this;
    }

    public Integer getRemainingVisits() {
        return remainingVisits;
    }

    public UserResponseDto setRemainingVisits(Integer remainingVisits) {
        this.remainingVisits = remainingVisits;
        return this;
    }

    public LocalDateTime getLastCheckInAt() {
        return lastCheckInAt;
    }

    public UserResponseDto setLastCheckInAt(LocalDateTime lastCheckInAt) {
        this.lastCheckInAt = lastCheckInAt;
        return this;
    }
}
