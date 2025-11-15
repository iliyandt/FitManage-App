package demos.springdata.fitmanage.domain.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailValidationRequest(
        @NotNull
        @NotBlank(message = "Please enter valid email")
        String email
) {

}
