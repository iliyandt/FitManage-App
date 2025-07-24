package demos.springdata.fitmanage.domain.dto.gymmember.response;

import demos.springdata.fitmanage.annotation.DropDown;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

import java.time.LocalDate;

public class GymMemberTableDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Employment employment;
    private LocalDate birthDate;
    private Integer visitLimit;
    private SubscriptionStatus subscriptionStatus;
    @DropDown(url = "/v1/SubscriptionPlan/values")
    private SubscriptionPlan subscriptionPlan;
    private String phone;
    private Integer allowedVisits;
    private Integer remainingVisits;

    public GymMemberTableDto() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Employment getEmployment() {
        return employment;
    }

    public void setEmployment(Employment employment) {
        this.employment = employment;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getVisitLimit() {
        return visitLimit;
    }

    public void setVisitLimit(Integer visitLimit) {
        this.visitLimit = visitLimit;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAllowedVisits() {
        return allowedVisits;
    }

    public void setAllowedVisits(Integer allowedVisits) {
        this.allowedVisits = allowedVisits;
    }

    public Integer getRemainingVisits() {
        return remainingVisits;
    }

    public void setRemainingVisits(Integer remainingVisits) {
        this.remainingVisits = remainingVisits;
    }
}