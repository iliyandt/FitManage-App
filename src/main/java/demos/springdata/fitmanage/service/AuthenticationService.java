package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.RegisterResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponse;
import demos.springdata.fitmanage.domain.dto.tenant.TenantRegisterRequest;
import demos.springdata.fitmanage.security.UserData;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    RegisterResponse registerUser(UserRegisterRequest userRegisterRequest, TenantRegisterRequest tenantRequest);
    EmailResponse findUserEmail(EmailValidationRequest emailValidationRequest);
    UserDetails authenticateUser(LoginRequest loginRequest);
    VerificationResponse verifyUserRegistration(VerificationRequest verificationRequest);
    VerificationResponse resendUserVerificationCode(String email);
    String changePassword(UserData userData, ChangePasswordRequest request);

}
