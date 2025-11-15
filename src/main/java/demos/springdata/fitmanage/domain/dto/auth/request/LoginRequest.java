package demos.springdata.fitmanage.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    @NotNull
    @NotBlank(message = "Please enter valid email")
    private String email;
    @NotBlank(message = "Please enter valid password.")
    private String password;
    private boolean enabled;
}
