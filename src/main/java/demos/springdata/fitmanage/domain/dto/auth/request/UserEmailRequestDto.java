package demos.springdata.fitmanage.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserEmailRequestDto {
    @NotNull
    @NotBlank(message = "Please enter valid email")
    private String email;

    public UserEmailRequestDto() {
    }

    public UserEmailRequestDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
