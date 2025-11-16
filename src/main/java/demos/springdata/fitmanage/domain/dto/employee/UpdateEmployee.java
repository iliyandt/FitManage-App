package demos.springdata.fitmanage.domain.dto.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.domain.enums.Gender;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateEmployee(
        String firstName,
        String lastName,
        String email,
        Gender gender,
        OffsetDateTime birthDate,
        String phone,
        EmployeeRole employeeRole
) {

}
