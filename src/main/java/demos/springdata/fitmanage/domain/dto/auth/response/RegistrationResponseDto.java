package demos.springdata.fitmanage.domain.dto.auth.response;

public class RegistrationResponseDto {

    private String username;
    private String email;
    private String verificationCode;

    public RegistrationResponseDto() {
    }

    public RegistrationResponseDto(String username, String email, String verificationCode) {
        this.username = username;
        this.email = email;
        this.verificationCode = verificationCode;
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



    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

}
