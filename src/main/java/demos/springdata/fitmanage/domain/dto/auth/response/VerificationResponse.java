package demos.springdata.fitmanage.domain.dto.auth.response;

public record VerificationResponse(
        String message,
        boolean success
) {
}
