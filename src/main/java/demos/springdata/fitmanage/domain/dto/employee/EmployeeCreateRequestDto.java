package demos.springdata.fitmanage.domain.dto.employee;

import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;

public class EmployeeCreateRequestDto extends UserCreateRequestDto {
    private EmployeeRole employeeRole;

    public EmployeeCreateRequestDto() {
    }

    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    public EmployeeCreateRequestDto setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
        return this;
    }
}
