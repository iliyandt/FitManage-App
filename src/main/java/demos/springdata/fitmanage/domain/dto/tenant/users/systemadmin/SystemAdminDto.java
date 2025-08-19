package demos.springdata.fitmanage.domain.dto.tenant.users.systemadmin;

public class SystemAdminDto {
    private Long id;
    private String email;
    private String password;

    public SystemAdminDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
