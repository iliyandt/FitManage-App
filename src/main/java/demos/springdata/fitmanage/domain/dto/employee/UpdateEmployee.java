package demos.springdata.fitmanage.domain.dto.employee;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.domain.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record UpdateEmployee(

        @Size(min = 2, max = 15, message = "First Name must be between 2 and 15 characters")
        @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with a capital letter and contain only letters")
        String firstName,

        @Size(min = 2, max = 30, message = "Last Name must be between 2 and 30 characters")
        @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with a capital letter and contain only letters")
        String lastName,

        @Email(message = "Email must be valid")
        String email,
        Gender gender,
        OffsetDateTime birthDate,

        @Pattern(
                regexp = "^\\+?[0-9]{7,15}$",
                message = "Phone number must be 7 to 15 digits and may start with '+'"
        )
        String phone,
        EmployeeRole employeeRole
) {

}
