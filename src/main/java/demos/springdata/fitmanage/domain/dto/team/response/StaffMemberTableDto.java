package demos.springdata.fitmanage.domain.dto.team.response;

import demos.springdata.fitmanage.annotation.DropDown;
import demos.springdata.fitmanage.domain.entity.StaffMember;

public class StaffMemberTableDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    @DropDown(url = "/v1/staff-members/staff/roles")
    private Long staffRoleId;
    private String staffRoleName;
    private String roleSelection;

    public StaffMemberTableDto() {
    }

    public static StaffMemberTableDto from(StaffMember s) {
        return new StaffMemberTableDto()
                .setId(s.getId())
                .setFirstName(s.getFirstName())
                .setLastName(s.getLastName())
                .setPhone(s.getPhone())
                .setStaffRoleId(s.getStaffRole().getId())
                .setStaffRoleName(s.getStaffRole().getName());
    }

    public StaffMemberTableDto setId(Long id) {
        this.id = id;
        return this;
    }

    public StaffMemberTableDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public StaffMemberTableDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public StaffMemberTableDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public StaffMemberTableDto setStaffRoleId(Long staffRoleId) {
        this.staffRoleId = staffRoleId;
        return this;
    }

    public StaffMemberTableDto setStaffRoleName(String staffRoleName) {
        this.staffRoleName = staffRoleName;
        return this;
    }


    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getStaffRoleId() {
        return staffRoleId;
    }

    public String getStaffRoleName() {
        return staffRoleName;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleSelection() {
        return roleSelection;
    }

    public void setRoleSelection(String roleSelection) {
        this.roleSelection = roleSelection;
    }
}


