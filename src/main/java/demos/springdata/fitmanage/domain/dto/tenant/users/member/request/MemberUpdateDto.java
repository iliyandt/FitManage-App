package demos.springdata.fitmanage.domain.dto.tenant.users.member.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public class MemberUpdateDto extends UserUpdateDto {


    @Size(min = 2, max = 15)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with a capital letter and contain only letters")
    private String firstName;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with a capital letter and contain only letters")
    private String lastName;
    @Email(message = "Email must be valid")
    private String email;
    private Employment employment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime birthDate;
    private SubscriptionPlan subscriptionPlan;

    public MemberUpdateDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public MemberUpdateDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public MemberUpdateDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public MemberUpdateDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public MemberUpdateDto setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public MemberUpdateDto setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public MemberUpdateDto setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
        return this;
    }
}
