package demos.springdata.fitmanage.domain.dto.auth.request;

public record VerificationRequest(
        String email,
        String verificationCode
) {
}
