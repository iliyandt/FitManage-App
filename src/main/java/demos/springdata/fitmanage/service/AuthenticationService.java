package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.authenticationDto.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.VerifyGymDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    Gym registerGym(GymRegistrationRequestDto gymRegistrationRequestDto);
    Gym validateEmail(GymEmailRequestDto gymEmailRequestDto);
    UserDetails authenticate(LoginRequestDto loginRequestDto);
    void verifyUser(VerifyGymDto verifyGymDto);
    void resendVerificationCode(String email);

}
