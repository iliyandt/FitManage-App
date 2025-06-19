package demos.springdata.fitmanage.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequestDto {
    @NotNull
    @NotBlank(message = "Please enter valid email")
    private String email;

    @NotBlank(message = "Please enter valid password.")
    private String password;
    private boolean enabled;

    public LoginRequestDto() {
    }


    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
