package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegisterResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponse;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    RegisterResponse registerUser(RegisterRequest registerRequest, TenantDto tenantDto);
    EmailResponseDto findUserEmail(EmailValidationRequest emailValidationRequest);
    UserDetails authenticateUser(LoginRequest loginRequest);
    VerificationResponse verifyUserRegistration(VerificationRequest verificationRequest);
    VerificationResponse resendUserVerificationCode(String email);
    String changePassword(ChangePasswordRequest request);

}
