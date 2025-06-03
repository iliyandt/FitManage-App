package demos.springdata.fitmanage.domain.dto.authenticationDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GymEmailRequestDto {
    @NotNull
    @NotBlank(message = "Please enter valid email")
    private String email;

    public GymEmailRequestDto() {
    }

    public GymEmailRequestDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
