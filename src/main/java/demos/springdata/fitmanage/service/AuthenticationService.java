package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.auth.request.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.RegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.VerificationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.response.GymEmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponseDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface AuthenticationService {
    RegistrationResponseDto registerGym(RegistrationRequestDto registrationRequestDto);
    Optional<GymEmailResponseDto> validateEmail(GymEmailRequestDto gymEmailRequestDto);
    UserDetails login(LoginRequestDto loginRequestDto);
    VerificationResponseDto verifyUser(VerificationRequestDto verificationRequestDto);
    VerificationResponseDto resendVerificationCode(String email);

}
