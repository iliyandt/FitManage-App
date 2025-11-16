package demos.springdata.fitmanage.domain.dto.member.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdate {
    @Size(min = 2, max = 15)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with a capital letter and contain only letters")
    private String firstName;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with a capital letter and contain only letters")
    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime birthDate;
    private String username;
    private String phone;
    private String address;
    private String city;
    private Gender gender;
    private String email;
    private Employment employment;
    private SubscriptionPlan subscriptionPlan;
}