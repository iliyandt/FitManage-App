package demos.springdata.fitmanage.domain.dto.auth.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailResponse(
        @NotNull
        @NotBlank(message = "Please enter valid email")
        String email
) {
        public EmailResponse {
        }
}
