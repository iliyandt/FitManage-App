package demos.springdata.fitmanage.domain.dto.employee;

import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeTable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private OffsetDateTime birthDate;
    private String phone;
    private EmployeeRole employeeRole;
}
