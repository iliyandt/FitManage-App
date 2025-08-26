package demos.springdata.fitmanage.domain.dto.member.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.dto.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;

import java.time.OffsetDateTime;

public class MemberUpdateDto extends UserUpdateDto {
    private String email;
    private Employment employment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime birthDate;
    private SubscriptionPlan subscriptionPlan;

    public MemberUpdateDto() {
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
