package demos.springdata.fitmanage.domain.dto.employee;

import demos.springdata.fitmanage.domain.dto.users.UserTableDto;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EmployeeTableDto extends UserTableDto {
    private EmployeeRole employeeRole;
}
