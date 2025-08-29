package demos.springdata.fitmanage.domain.dto.users;

import demos.springdata.fitmanage.domain.enums.EmployeeRole;

public class StaffResponseDto extends UserBaseResponseDto {
    private Integer membersCount;
    private EmployeeRole employeeRole;

    public StaffResponseDto() {
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public StaffResponseDto setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
        return this;
    }

    public EmployeeRole getStaffRole() {
        return employeeRole;
    }

    public StaffResponseDto setStaffRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
        return this;
    }
}
