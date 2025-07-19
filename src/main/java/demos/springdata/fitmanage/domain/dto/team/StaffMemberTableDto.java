package demos.springdata.fitmanage.domain.dto.team;

import demos.springdata.fitmanage.annotation.DropDown;
import demos.springdata.fitmanage.domain.entity.StaffRole;

public class StaffMemberTableDto {
    private Long id;
    private String firstName;
    private String lastName;

    @DropDown(url = "/v1/staff-members/staff/roles")
    private Long staffRoleId;

    private String staffRoleName;
    private String phone;

    public StaffMemberTableDto() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getStaffRoleId() {
        return staffRoleId;
    }

    public void setStaffRoleId(Long staffRoleId) {
        this.staffRoleId = staffRoleId;
    }

    public String getStaffRoleName() {
        return staffRoleName;
    }

    public void setStaffRoleName(String staffRoleName) {
        this.staffRoleName = staffRoleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

