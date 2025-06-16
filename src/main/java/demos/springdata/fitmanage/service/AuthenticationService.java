package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.auth.*;
import demos.springdata.fitmanage.domain.entity.Gym;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    RegistrationResponseDto registerGym(RegistrationRequestDto registrationRequestDto);
    Gym validateEmail(GymEmailRequestDto gymEmailRequestDto);
    UserDetails authenticate(LoginRequestDto loginRequestDto);
    VerificationResponseDto verifyUser(VerifyGymDto verifyGymDto);
    void resendVerificationCode(String email);

}
