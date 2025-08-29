package demos.springdata.fitmanage.domain.dto.staff;

import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.enums.StaffRole;

public class StaffCreateRequestDto extends UserCreateRequestDto {
    private StaffRole staffRole;

    public StaffCreateRequestDto() {
    }

    public StaffRole getStaffRole() {
        return staffRole;
    }

    public StaffCreateRequestDto setStaffRole(StaffRole staffRole) {
        this.staffRole = staffRole;
        return this;
    }
}
