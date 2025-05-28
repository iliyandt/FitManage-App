package demos.springdata.fitmanage.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GymLoginRequestDto {
    private String email;
    private String password;

    public GymLoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @NotNull
    @NotBlank(message = "Please enter valid email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotNull
    @NotBlank(message = "Please enter valid email")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
