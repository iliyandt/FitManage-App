package demos.springdata.fitmanage.domain.dto.team.response;

public class StaffMemberResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String roleName;
    private boolean enabled;
    private String gymName;
    private boolean passwordGenerated;
    private String passwordNote;


    public StaffMemberResponseDto() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public boolean isPasswordGenerated() {
        return passwordGenerated;
    }

    public void setPasswordGenerated(boolean passwordGenerated) {
        this.passwordGenerated = passwordGenerated;
    }

    public String getPasswordNote() {
        return passwordNote;
    }

    public void setPasswordNote(String passwordNote) {
        this.passwordNote = passwordNote;
    }
}
