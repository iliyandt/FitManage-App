package demos.springdata.fitmanage.domain.dto.gymmember.response;

import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


public class GymMemberResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Employment employment;
    private OffsetDateTime birthDate;
    private Integer visitLimit;
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

    public GymMemberResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public GymMemberResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public GymMemberResponseDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public GymMemberResponseDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public GymMemberResponseDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public GymMemberResponseDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public GymMemberResponseDto setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public GymMemberResponseDto setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Integer getVisitLimit() {
        return visitLimit;
    }

    public GymMemberResponseDto setVisitLimit(Integer visitLimit) {
        this.visitLimit = visitLimit;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public GymMemberResponseDto setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public GymMemberResponseDto setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public GymMemberResponseDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public GymMemberResponseDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public GymMemberResponseDto setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
        return this;
    }

    public LocalDateTime getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public GymMemberResponseDto setSubscriptionStartDate(LocalDateTime subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
        return this;
    }

    public LocalDateTime getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public GymMemberResponseDto setSubscriptionEndDate(LocalDateTime subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
        return this;
    }

    public Integer getAllowedVisits() {
        return allowedVisits;
    }

    public GymMemberResponseDto setAllowedVisits(Integer allowedVisits) {
        this.allowedVisits = allowedVisits;
        return this;
    }

    public Integer getRemainingVisits() {
        return remainingVisits;
    }

    public GymMemberResponseDto setRemainingVisits(Integer remainingVisits) {
        this.remainingVisits = remainingVisits;
        return this;
    }

    public LocalDateTime getLastCheckInAt() {
        return lastCheckInAt;
    }

    public GymMemberResponseDto setLastCheckInAt(LocalDateTime lastCheckInAt) {
        this.lastCheckInAt = lastCheckInAt;
        return this;
    }
}
