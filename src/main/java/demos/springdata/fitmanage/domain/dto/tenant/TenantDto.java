package demos.springdata.fitmanage.domain.dto.tenant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantDto {
    private Long id;
    private String stripeAccountId;
    @NotNull
    private String name;
    @Email
    private String businessEmail;
    @NotNull
    private String address;
    @NotNull
    private String city;
    private String abonnement;
    private String abonnementDuration;
    private LocalDate subscriptionValidUntil;
    private Long membersCount;
}
