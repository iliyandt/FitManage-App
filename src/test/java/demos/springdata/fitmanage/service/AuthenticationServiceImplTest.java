package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.authenticationDto.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.VerifyGymDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.impl.AuthenticationServiceImpl;
import demos.springdata.fitmanage.util.ValidationUtil;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private GymRepository gymRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ValidationUtil validationUtil;

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

    private GymRegistrationRequestDto validGymRegistrationDto;

    @BeforeEach
    void setup() {
        validGymRegistrationDto = new GymRegistrationRequestDto();
        validGymRegistrationDto.setUsername("gym1");
        validGymRegistrationDto.setEmail("email@gym.com");
        validGymRegistrationDto.setPassword("pass123");
        validGymRegistrationDto.setConfirmPassword("pass123");
    }


    @Test
    void registerGym_ShouldSaveGym_WhenValidInput() {
        when(validationUtil.isValid(validGymRegistrationDto)).thenReturn(true);
        when(gymRepository.findByUsername(validGymRegistrationDto.getUsername())).thenReturn(Optional.empty());
        when(gymRepository.findByEmail(validGymRegistrationDto.getEmail())).thenReturn(Optional.empty());

        Gym mappedGym = new Gym();
        mappedGym.setUsername(validGymRegistrationDto.getUsername());
        mappedGym.setEmail(validGymRegistrationDto.getEmail());
        mappedGym.setPassword(validGymRegistrationDto.getPassword());


        when(modelMapper.map(validGymRegistrationDto, Gym.class)).thenReturn(mappedGym);
        when(passwordEncoder.encode(validGymRegistrationDto.getPassword())).thenReturn("encryptedPass");

        Role gymAdminRole = new Role();
        gymAdminRole.setName(RoleType.GYM_ADMIN);
        when(roleService.findByName(RoleType.GYM_ADMIN)).thenReturn(gymAdminRole);

        Gym result = authenticationService.registerGym(validGymRegistrationDto);

        Assertions.assertNotNull(result);
        assertEquals("encryptedPass", result.getPassword());
        Assertions.assertFalse(result.isEnabled());
        Assertions.assertNotNull(result.getVerificationCode());
        Assertions.assertNotNull(result.getVerificationCodeExpiresAt());
        Assertions.assertTrue(result.getRoles().contains(gymAdminRole));
    }

    @Test
    void registerGym_ShouldThrowException_WhenUsernameExists() {
        when(validationUtil.isValid(validGymRegistrationDto)).thenReturn(true);
        when(gymRepository.findByUsername(validGymRegistrationDto.getUsername())).thenReturn(Optional.of(new Gym()));

        FitManageAppException exception = Assertions.assertThrows(FitManageAppException.class,
                () -> authenticationService.registerGym(validGymRegistrationDto));

        Assertions.assertEquals("Gym with this name already exists", exception.getMessage());
    }

    @Test
    void registerGym_ShouldThrowException_WhenEmailExists() {
        Mockito.when(validationUtil.isValid(validGymRegistrationDto)).thenReturn(true);
        Mockito.when(gymRepository.findByEmail(validGymRegistrationDto.getEmail())).thenReturn(Optional.of(new Gym()));

        FitManageAppException exception = Assertions.assertThrows(FitManageAppException.class,
                () -> authenticationService.registerGym(validGymRegistrationDto));

        Assertions.assertEquals("Email is already registered", exception.getMessage());
    }


    @Test
    void registerGym_ShouldThrowException_WhenPasswordsDoNotMatch() {

        validGymRegistrationDto.setConfirmPassword("differentPassword");
        when(validationUtil.isValid(validGymRegistrationDto)).thenReturn(true);
        when(gymRepository.findByUsername(validGymRegistrationDto.getUsername())).thenReturn(Optional.empty());
        when(gymRepository.findByEmail(validGymRegistrationDto.getEmail())).thenReturn(Optional.empty());

        FitManageAppException exception = Assertions.assertThrows(FitManageAppException.class,
                () -> authenticationService.registerGym(validGymRegistrationDto));

        Assertions.assertEquals("Passwords do not match", exception.getMessage());
    }

    @Test
    void validateEmail_ShouldReturnGym_WhenEmailExists() {
        String email = "email@gym.com";
        Gym gym = new Gym();
        gym.setEmail(email);

        when(validationUtil.isValid(any())).thenReturn(true);
        when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        Gym result = authenticationService.validateEmail(new GymEmailRequestDto(email));

        Assertions.assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void authenticate_ShouldAuthenticateUser_WhenValid() {
        String email = "email@gym.com";
        String password = "pass123";

        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setEnabled(true);

        Mockito.when(validationUtil.isValid(loginRequestDto)).thenReturn(true);
        Mockito.when(customUserDetailsService.loadUserByUsername(email)).thenReturn(gym);

        UserDetails userDetails = authenticationService.authenticate(loginRequestDto);

        Assertions.assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void verifyUser_ShouldEnableGym_WhenCodeIsCorrectAndNotExpired() {
        String email = "test@example.com";
        String code = "123456";
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(10);

        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setVerificationCode(code);
        gym.setVerificationCodeExpiresAt(futureTime);
        gym.setEnabled(false);

        VerifyGymDto dto = new VerifyGymDto();
        dto.setEmail(email);
        dto.setVerificationCode(code);

        Mockito.when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        authenticationService.verifyUser(dto);

        Assertions.assertTrue(gym.isEnabled());
        Assertions.assertNull(gym.getVerificationCode());
        Assertions.assertNull(gym.getVerificationCodeExpiresAt());

        Mockito.verify(gymRepository).save(gym);
    }

    @Test
    void verifyUser_ShouldThrowException_WhenCodeIsInvalid() {
        String email = "test@example.com";
        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setVerificationCode("correctCode");
        gym.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        VerifyGymDto dto = new VerifyGymDto();
        dto.setEmail(email);
        dto.setVerificationCode("wrongCode");

        when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.verifyUser(dto));

        assertEquals("Invalid verification code", exception.getMessage());
    }

    @Test
    void verifyUser_ShouldThrowException_WhenCodeIsExpired() {
        String email = "test@example.com";
        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setVerificationCode("123456");
        gym.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));

        VerifyGymDto dto = new VerifyGymDto();
        dto.setEmail(email);
        dto.setVerificationCode("123456");

        when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.verifyUser(dto));

        assertEquals("Verification code has expired", exception.getMessage());
    }

    @Test
    void verifyUser_ShouldThrowException_WhenUserNotFound() {
        String email = "notfound@example.com";
        VerifyGymDto dto = new VerifyGymDto();
        dto.setEmail(email);
        dto.setVerificationCode("123456");

        when(gymRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.verifyUser(dto));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void resendVerificationCode_ShouldGenerateNewCode_WhenUserExistsAndNotEnabled() throws MessagingException {

        String email = "test@example.com";
        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setEnabled(false);

        Mockito.when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        authenticationService.resendVerificationCode(email);

        Assertions.assertNotNull(gym.getVerificationCode());
        Assertions.assertNotNull(gym.getVerificationCodeExpiresAt());
        Assertions.assertTrue(gym.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()));

        Mockito.verify(emailService).sendVerificationEmail(Mockito.eq(email), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(gymRepository).save(gym);
    }

    @Test
    void resendVerificationCode_ShouldThrowException_WhenAccountIsAlreadyVerified() throws MessagingException {
        String email = "test@example.com";
        Gym gym = new Gym();
        gym.setEmail(email);
        gym.setEnabled(true);

        when(gymRepository.findByEmail(email)).thenReturn(Optional.of(gym));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.resendVerificationCode(email));

        assertEquals("Account is already verified", exception.getMessage());

        Mockito.verify(emailService, Mockito.never()).sendVerificationEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(gymRepository, Mockito.never()).save(any());
    }

    @Test
    void resendVerificationCode_ShouldThrowException_WhenUserNotFound() throws MessagingException {
        String email = "missing@example.com";

        when(gymRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationService.resendVerificationCode(email));

        assertEquals("User not found", exception.getMessage());

        Mockito.verify(emailService, Mockito.never()).sendVerificationEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(gymRepository, Mockito.never()).save(any());
    }
}
