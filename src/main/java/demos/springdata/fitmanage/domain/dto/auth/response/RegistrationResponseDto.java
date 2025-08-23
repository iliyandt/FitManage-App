package demos.springdata.fitmanage.domain.dto.auth.response;

public class RegistrationResponseDto {

    private String email;
    private String verificationCode;

    public RegistrationResponseDto() {
    }

    public RegistrationResponseDto(String email, String verificationCode) {
        this.email = email;
        this.verificationCode = verificationCode;
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
