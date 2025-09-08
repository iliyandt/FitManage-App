package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    RegistrationResponseDto registerUser(RegistrationRequestDto registrationRequestDto, TenantDto tenantDto);
    EmailResponseDto checkIfEmailIsAvailable(UserEmailRequestDto userEmailRequestDto);
    UserDetails authenticateUser(LoginRequestDto loginRequestDto);
    VerificationResponseDto verifyUserRegistration(VerificationRequestDto verificationRequestDto);
    VerificationResponseDto resendUserVerificationCode(String email);
    String changePassword(ChangePasswordRequest request);

}
