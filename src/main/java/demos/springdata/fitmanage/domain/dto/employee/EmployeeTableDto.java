package demos.springdata.fitmanage.domain.dto.employee;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import demos.springdata.fitmanage.domain.dto.users.UserTableDto;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder(alphabetic = true)
public class EmployeeTableDto extends UserTableDto {
    private EmployeeRole employeeRole;
}
