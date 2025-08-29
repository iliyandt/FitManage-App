package demos.springdata.fitmanage.domain.dto.users;

import demos.springdata.fitmanage.domain.enums.StaffRole;

public class StaffResponseDto extends UserBaseResponseDto {
    private Integer membersCount;
    private StaffRole staffRole;

    public StaffResponseDto() {
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public StaffResponseDto setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
        return this;
    }

    public StaffRole getStaffRole() {
        return staffRole;
    }

    public StaffResponseDto setStaffRole(StaffRole staffRole) {
        this.staffRole = staffRole;
        return this;
    }
}
