package demos.springdata.fitmanage.domain.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUser {
    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 15, message = "First Name must be between 2 and 15 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with a capital letter and contain only letters")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(min = 2, max = 30, message = "Last Name must be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with a capital letter and contain only letters")
    private String lastName;

    @Size(min = 2, max = 15, message = "Username must be  between 2 and 15 characters")
    private String username;

    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Choose gender")
    private Gender gender;

    @NotNull(message = "Birth Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @PastOrPresent(message = "Birth Date cannot be in the future")
    private OffsetDateTime birthDate;

    @NotBlank(message = "Phone must not be blank")
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must be 7 to 15 digits and may start with '+'"
    )
    private String phone;

}
