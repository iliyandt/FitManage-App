package demos.springdata.fitmanage.domain.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailValidationRequest(
        @NotNull
        @Email(message = "Please enter valid email address.")
        String email
) {

}
