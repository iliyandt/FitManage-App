package demos.springdata.fitmanage.domain.dto.tenant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantRegisterRequest {
    @NotNull
    private String name;
    @Email
    private String businessEmail;
    @NotNull
    private String address;
    @NotNull
    private String city;
}
