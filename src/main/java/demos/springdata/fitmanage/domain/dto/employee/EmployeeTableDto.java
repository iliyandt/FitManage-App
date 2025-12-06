package demos.springdata.fitmanage.domain.dto.employee;

import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeTableDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Gender gender;
    private OffsetDateTime birthDate;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    

    private EmployeeRole employeeRole;
}
