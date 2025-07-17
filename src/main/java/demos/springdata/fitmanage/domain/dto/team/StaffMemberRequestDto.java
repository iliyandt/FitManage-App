package demos.springdata.fitmanage.domain.dto.team;



public class StaffMemberRequestDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long staffRoleId;
    private Long predefinedRoleId;
    private String customRoleName;

    public StaffMemberRequestDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getStaffRoleId() {
        return staffRoleId;
    }

    public void setStaffRoleId(Long staffRoleId) {
        this.staffRoleId = staffRoleId;
    }

    public Long getPredefinedRoleId() {
        return predefinedRoleId;
    }

    public void setPredefinedRoleId(Long predefinedRoleId) {
        this.predefinedRoleId = predefinedRoleId;
    }

    public String getCustomRoleName() {
        return customRoleName;
    }

    public void setCustomRoleName(String customRoleName) {
        this.customRoleName = customRoleName;
    }
}
