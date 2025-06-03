package demos.springdata.fitmanage.domain.dto.authenticationDto;

public class VerifyGymDto {
    private String email;
    private String verificationCode;

    public VerifyGymDto() {
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
