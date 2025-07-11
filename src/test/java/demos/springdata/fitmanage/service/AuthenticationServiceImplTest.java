package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.auth.request.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.RegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.request.VerificationRequestDto;
import demos.springdata.fitmanage.domain.dto.auth.response.GymEmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.impl.AuthenticationServiceImpl;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private GymRepository gymRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegistrationRequestDto validGymRegistrationDto;

    @BeforeEach
    void setup() {
        validGymRegistrationDto = new RegistrationRequestDto();
        validGymRegistrationDto.setUsername("gym1");
        validGymRegistrationDto.setEmail("email@gym.com");
        validGymRegistrationDto.setPassword("pass123");
        validGymRegistrationDto.setConfirmPassword("pass123");
    }


    @Test
    void registerGym_ShouldSaveGym_WhenValidInput() {
        Role gymAdminRole = new Role(RoleType.GYM_ADMIN);
        when(roleService.findByName(RoleType.GYM_ADMIN)).thenReturn(gymAdminRole);

        Gym mappedGym = new Gym();
        mappedGym.setUsername(validGymRegistrationDto.getUsername());
        mappedGym.setEmail(validGymRegistrationDto.getEmail());
        mappedGym.setPassword("plainPassword");

        when(modelMapper.map(any(RegistrationRequestDto.class), eq(Gym.class))).thenReturn(mappedGym);

        Gym savedGym = new Gym();
        savedGym.setUsername(validGymRegistrationDto.getUsername());
        savedGym.setEmail(validGymRegistrationDto.getEmail());
        savedGym.setVerificationCode("123456");

        when(gymRepository.save(any(Gym.class))).thenReturn(savedGym);

        RegistrationResponseDto response = authenticationService.registerGym(validGymRegistrationDto);

        assertNotNull(response);
        assertEquals(validGymRegistrationDto.getUsername(), response.getUsername());
        assertEquals(validGymRegistrationDto.getEmail(), response.getEmail());
        assertNotNull(response.getVerificationCode());
    }

//    @Test
//    void registerGym_ShouldThrowException_WhenUsernameExists() {
//    }

//    @Test
//    void registerGym_ShouldThrowException_WhenEmailExists() {
//    }


//    @Test
//    void registerGym_ShouldThrowException_WhenPasswordsDoNotMatch() {
//    }

    @Test
    void checkIfEmail_ShouldReturnGym_WhenEmailIsAvailableExists() {
        String email = "email@gym.com";
        Gym gym = new Gym();
        gym.setEmail(email);

        GymEmailResponseDto responseDto = new GymEmailResponseDto();
        responseDto.setEmail(email);

        when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));
        when(modelMapper.map(any(Gym.class), eq(GymEmailResponseDto.class))).thenReturn(responseDto);

        Optional<GymEmailResponseDto> result = authenticationService.checkIfEmailIsAvailable(new GymEmailRequestDto(email));

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(email, result.get().getEmail());
    }

//    @Test
//    void authenticate_ShouldLoginUser_WhenValid() {
//    }

    @Test
    void verifyUser_Registration_ShouldEnableGym_WhenCodeIsCorrectAndNotExpired() {
        String email = "test@example.com";
        String code = "123456";
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(10);

        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setVerificationCode(code);
        gym.setVerificationCodeExpiresAt(futureTime);
        gym.setEnabled(false);

        VerificationRequestDto dto = new VerificationRequestDto();
        dto.setEmail(email);
        dto.setVerificationCode(code);

        Mockito.when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        authenticationService.verifyUserRegistration(dto);

        Assertions.assertTrue(gym.isEnabled());
        Assertions.assertNull(gym.getVerificationCode());
        Assertions.assertNull(gym.getVerificationCodeExpiresAt());

        verify(gymRepository).save(gym);
    }

    @Test
    void verifyUser_Registration_ShouldThrowException_WhenCodeIsInvalid() {
    }

//    @Test
//    void verifyUser_ShouldThrowException_WhenCodeIsExpired() {
//    }

    @Test
    void verifyUser_ShouldThrowException_WhenUserRegistrationNotFound() {
        String email = "notfound@example.com";
        VerificationRequestDto dto = new VerificationRequestDto();
        dto.setEmail(email);
        dto.setVerificationCode("123456");

        when(gymRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.verifyUserRegistration(dto));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void resendUserVerificationCode_ShouldGenerateNewCode_WhenUserExistsAndNotEnabled() throws MessagingException {

        String email = "test@example.com";
        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setEnabled(false);

        Mockito.when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        authenticationService.resendUserVerificationCode(email);

        assertNotNull(gym.getVerificationCode());
        assertNotNull(gym.getVerificationCodeExpiresAt());
        Assertions.assertTrue(gym.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()));

        verify(emailService).sendUserVerificationEmail(eq(email), Mockito.anyString(), Mockito.anyString());
        verify(gymRepository).save(gym);
    }

    @Test
    void resendUserVerificationCode_ShouldThrowException_WhenAccountIsAlreadyVerified() throws MessagingException {
        String email = "test@example.com";
        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setEnabled(true);

        when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.resendUserVerificationCode(email));

        assertEquals("Account is already verified", exception.getMessage());

        verify(emailService, Mockito.never()).sendUserVerificationEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(gymRepository, Mockito.never()).save(any());
    }

    @Test
    void resendUserVerificationCode_ShouldThrowException_WhenUserNotFound() throws MessagingException {
        String email = "missing@example.com";

        when(gymRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.resendUserVerificationCode(email));

        assertEquals("User not found", exception.getMessage());

        verify(emailService, Mockito.never()).sendUserVerificationEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(gymRepository, Mockito.never()).save(any());
    }

    @Test
    void authenticateUser_ShouldAuthenticateSuccessfully_WhenCredentialsAreValid() {
        String email = "email@gym.com";
        String password = "securePassword";

        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        Gym gymUser = new Gym();
        gymUser.setEmail(email);
        gymUser.setPassword(password);
        gymUser.setEnabled(true);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(gymUser);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        )).thenReturn(null);


        UserDetails result = authenticationService.authenticateUser(loginRequest);

        assertNotNull(result);
        assertEquals(email, result.getUsername());

        verify(customUserDetailsService).loadUserByUsername(email);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
