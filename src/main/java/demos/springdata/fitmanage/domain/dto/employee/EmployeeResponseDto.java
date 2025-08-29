package demos.springdata.fitmanage.domain.dto.employee;

import demos.springdata.fitmanage.domain.dto.users.UserBaseResponseDto;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;

public class EmployeeResponseDto extends UserBaseResponseDto {
    private Integer membersCount;
    private EmployeeRole employeeRole;

    public EmployeeResponseDto() {
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public EmployeeResponseDto setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
        return this;
    }

    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    public EmployeeResponseDto setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
        return this;
    }
}
