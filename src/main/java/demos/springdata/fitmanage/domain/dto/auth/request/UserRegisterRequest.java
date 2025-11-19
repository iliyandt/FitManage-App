package demos.springdata.fitmanage.domain.dto.auth.request;

import demos.springdata.fitmanage.domain.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegisterRequest {
    @NotBlank
    @Email(message = "Please enter valid email.")
    private String email;
    private Gender gender;
    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    @NotNull(message = "Confirm password should match password.")
    private String confirmPassword;
}
