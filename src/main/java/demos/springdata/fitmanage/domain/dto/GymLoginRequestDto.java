package demos.springdata.fitmanage.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GymLoginRequestDto {
    @NotNull
    @NotBlank(message = "Please enter valid email")
    private String email;

    @NotBlank(message = "Please enter valid password.")
    private String password;

    public GymLoginRequestDto(String email) {
        this.email = email;
    }

    public GymLoginRequestDto(String email, String password) {
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
}
