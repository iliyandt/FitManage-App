package demos.springdata.fitmanage.domain.dto.auth.response;

public record RegisterResponse(
        String email,
        String verificationCode
) {

}
