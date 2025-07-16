package demos.springdata.fitmanage.domain.dto.team;


import demos.springdata.fitmanage.domain.enums.StaffPosition;

public class StaffMemberRequestDto {
    private String username;
    private String password;
    private StaffPosition position;
    private String email;
    private String phone;
    private String gymUsername;

    public StaffMemberRequestDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public StaffPosition getPosition() {
        return position;
    }

    public void setPosition(StaffPosition position) {
        this.position = position;
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

    public String getGymUsername() {
        return gymUsername;
    }

    public void setGymUsername(String gymUsername) {
        this.gymUsername = gymUsername;
    }

    public void setGym(String gymUsername) {
        this.gymUsername = gymUsername;
    }
}
