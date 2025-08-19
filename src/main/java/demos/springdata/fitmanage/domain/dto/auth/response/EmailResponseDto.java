package demos.springdata.fitmanage.domain.dto.auth.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmailResponseDto {
    @NotNull
    @NotBlank(message = "Please enter valid email")
    private String email;

    public EmailResponseDto() {
    }

    public EmailResponseDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
