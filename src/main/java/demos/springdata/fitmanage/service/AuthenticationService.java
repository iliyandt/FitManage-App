package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.authenticationDto.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.GymLoginRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.VerifyGymDto;
import demos.springdata.fitmanage.domain.entity.Gym;

public interface AuthenticationService {
    Gym registerGym(GymRegistrationRequestDto gymRegistrationRequestDto);
    Gym validateEmail(GymEmailRequestDto gymEmailRequestDto);
    Gym authenticate(GymLoginRequestDto gymLoginRequestDto);
    void verifyUser(VerifyGymDto verifyGymDto);
    void resendVerificationCode(String email);

}
