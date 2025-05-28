package demos.springdata.fitmanage.service;


import demos.springdata.fitmanage.domain.dto.GymRegistrationRequestDto;

public interface AuthenticationService {
    void registerGym(GymRegistrationRequestDto gymRegistrationRequestDto);
    void loginUser(GymRegistrationRequestDto gymRegistrationRequestDto);
}
