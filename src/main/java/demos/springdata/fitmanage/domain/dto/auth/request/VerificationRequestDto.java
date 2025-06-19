package demos.springdata.fitmanage.domain.dto.auth.request;

public class VerificationRequestDto {
    private String email;
    private String verificationCode;

    public VerificationRequestDto() {
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
