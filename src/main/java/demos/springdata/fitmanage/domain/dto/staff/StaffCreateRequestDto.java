package demos.springdata.fitmanage.domain.dto.staff;

import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;

public class StaffCreateRequestDto extends UserCreateRequestDto {
    private EmployeeRole employeeRole;

    public StaffCreateRequestDto() {
    }

    public EmployeeRole getStaffRole() {
        return employeeRole;
    }

    public StaffCreateRequestDto setStaffRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
        return this;
    }
}
