package demos.springdata.fitmanage.domain.dto.superadmin;

public class SuperAdminDto {
    private String email;
    private String password;

    public SuperAdminDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
