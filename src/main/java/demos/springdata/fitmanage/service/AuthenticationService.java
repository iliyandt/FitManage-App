package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.auth.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.RegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.VerifyGymDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    Gym registerGym(RegistrationRequestDto registrationRequestDto);
    Gym validateEmail(GymEmailRequestDto gymEmailRequestDto);
    UserDetails authenticate(LoginRequestDto loginRequestDto);
    void verifyUser(VerifyGymDto verifyGymDto);
    void resendVerificationCode(String email);

}
