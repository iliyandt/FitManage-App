package demos.springdata.fitmanage.domain.dto.member.request;
import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberFilter {
    private Long id;
    @Size(min = 2, max = 15)
    private String firstName;

    @Size(min = 2, max = 20)
    private String lastName;

    private Gender gender;

    private Employment employment;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime birthDate;

    @Column(unique = true)
    private String email;
    private SubscriptionStatus subscriptionStatus;
    private SubscriptionPlan subscriptionPlan;

    @Column(unique = true)
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must be 7 to 15 digits and may start with '+'"
    )
    private String phone;

    private String qrToken;
}
