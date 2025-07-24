package demos.springdata.fitmanage.domain.dto.gymmember.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class GymMemberCreateRequestDto {

    @NotBlank
    @Size(min = 2, max = 15)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with a capital letter and contain only letters")
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with a capital letter and contain only letters")
    private String lastName;

    private Gender gender;

    private Employment employment;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private Integer visitLimit;

    @NotBlank
    @Column(unique = true)
    @Email(message = "Email must be valid")
    private String email;
    private SubscriptionStatus subscriptionStatus;
    private SubscriptionPlan subscriptionPlan;
    @NotBlank
    @Column(unique = true)
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must be 7 to 15 digits and may start with '+'"
    )
    private String phone;

    public GymMemberCreateRequestDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public GymMemberCreateRequestDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public GymMemberCreateRequestDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public GymMemberCreateRequestDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public GymMemberCreateRequestDto setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public GymMemberCreateRequestDto setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Integer getVisitLimit() {
        return visitLimit;
    }

    public GymMemberCreateRequestDto setVisitLimit(Integer visitLimit) {
        this.visitLimit = visitLimit;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public GymMemberCreateRequestDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public GymMemberCreateRequestDto setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public GymMemberCreateRequestDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public GymMemberCreateRequestDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
