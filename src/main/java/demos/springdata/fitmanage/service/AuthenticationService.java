package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.auth.request.UserEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.RegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.VerificationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import org.springframework.security.core.userdetails.UserDetails;



public interface AuthenticationService {
    RegistrationResponseDto registerGym(RegistrationRequestDto registrationRequestDto, TenantDto tenantDto);
    EmailResponseDto checkIfEmailIsAvailable(UserEmailRequestDto userEmailRequestDto);
    UserDetails authenticateUser(LoginRequestDto loginRequestDto);
    VerificationResponseDto verifyUserRegistration(VerificationRequestDto verificationRequestDto);
    VerificationResponseDto resendUserVerificationCode(String email);

}
