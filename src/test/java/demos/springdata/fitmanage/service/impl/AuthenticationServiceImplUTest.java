package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.client.PaymentFeignClient;
import demos.springdata.fitmanage.domain.dto.auth.request.ChangePasswordRequest;
import demos.springdata.fitmanage.domain.dto.auth.request.LoginRequest;
import demos.springdata.fitmanage.domain.dto.auth.request.UserRegisterRequest;
import demos.springdata.fitmanage.domain.dto.auth.request.VerificationRequest;
import demos.springdata.fitmanage.domain.dto.auth.response.RegisterResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponse;
import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantRegisterRequest;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import demos.springdata.fitmanage.service.EmailService;
import org.hibernate.mapping.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplUTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private EmailService emailService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PaymentFeignClient paymentFeignClient;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;


    @Test
    void registerUser_ShouldRegisterSuccessfully_WhenDataIsValid() {
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setEmail("admin@gym.bg");
        userRequest.setPassword("password123");
        userRequest.setConfirmPassword("password123");

        TenantRegisterRequest tenantRequest = new TenantRegisterRequest();
        tenantRequest.setName("Power Gym");
        tenantRequest.setBusinessEmail("gym@business.bg");

        Tenant mockTenant = new Tenant();
        mockTenant.setId(UUID.randomUUID());
        mockTenant.setName("Power Gym");

        User mockUser = new User();
        mockUser.setEmail(userRequest.getEmail());
        mockUser.setVerificationCode("123456");


        when(userRepository.findByEmail(userRequest.getEmail())).thenReturn(Optional.empty());

        when(userMapper.toAdminUser(any(Tenant.class), eq(userRequest))).thenReturn(mockUser);

        RegisterResponse response = authenticationService.registerUser(userRequest, tenantRequest);

        assertNotNull(response);
        assertEquals(userRequest.getEmail(), response.email());


        verify(tenantRepository).save(any(Tenant.class));
        verify(paymentFeignClient).createConnectedAccount(any(TenantDto.class));
        verify(userRepository).save(mockUser);
        verify(emailService).sendVerificationEmail(mockUser);
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordsDoNotMatch() {

        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("pass1");
        request.setConfirmPassword("pass2");

        TenantRegisterRequest tenantRequest = new TenantRegisterRequest();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());


        assertThrows(MultipleValidationException.class, () ->
                authenticationService.registerUser(request, tenantRequest)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {

        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("existing@gym.bg");
        request.setPassword("pass");
        request.setConfirmPassword("pass");

        TenantRegisterRequest tenantRequest = new TenantRegisterRequest();


        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));


        assertThrows(MultipleValidationException.class, () ->
                authenticationService.registerUser(request, tenantRequest)
        );
    }


    @Test
    void authenticateUser_ShouldReturnUserDetails_WhenCredentialsValidAndUserEnabled() {

        LoginRequest loginRequest = new LoginRequest("admin@gym.bg", "password");

        UserData mockUserDetails = mock(UserData.class);
        when(mockUserDetails.isEnabled()).thenReturn(true);

        when(customUserDetailsService.loadUserByUsername(loginRequest.getEmail()))
                .thenReturn(mockUserDetails);


        UserDetails result = authenticationService.authenticateUser(loginRequest);


        assertEquals(mockUserDetails, result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenUserNotVerified() {

        LoginRequest loginRequest = new LoginRequest("new@gym.bg", "password");

        UserData mockUserDetails = mock(UserData.class);
        when(mockUserDetails.isEnabled()).thenReturn(false);

        when(customUserDetailsService.loadUserByUsername(loginRequest.getEmail()))
                .thenReturn(mockUserDetails);

        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                authenticationService.authenticateUser(loginRequest)
        );

        assertEquals("Account not verified. Please verify your account!", ex.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getErrorCode());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenBadCredentials() {

        LoginRequest loginRequest = new LoginRequest("admin@gym.bg", "wrong_pass");
        UserData mockUserDetails = mock(UserData.class);
        when(mockUserDetails.isEnabled()).thenReturn(true);

        when(customUserDetailsService.loadUserByUsername(loginRequest.getEmail()))
                .thenReturn(mockUserDetails);

        doThrow(new BadCredentialsException("Bad creds"))
                .when(authenticationManager).authenticate(any());


        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                authenticationService.authenticateUser(loginRequest)
        );
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getErrorCode());
    }


    @Test
    void verifyUserRegistration_ShouldEnableUser_WhenCodeIsValid() {

        String email = "test@gym.bg";
        String code = "123456";
        VerificationRequest request = new VerificationRequest(email, code);

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setVerificationCode(code);
        mockUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
        mockUser.setEnabled(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        VerificationResponse response = authenticationService.verifyUserRegistration(request);

        assertTrue(mockUser.isEnabled());
        assertNull(mockUser.getVerificationCode());

        verify(userRepository).save(mockUser);
    }

    @Test
    void verifyUserRegistration_ShouldThrow_WhenCodeExpired() {

        String email = "test@gym.bg";
        String code = "123456";
        VerificationRequest request = new VerificationRequest(email, code);

        User mockUser = new User();
        mockUser.setVerificationCode(code);
        mockUser.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));


        assertThrows(MultipleValidationException.class, () ->
                authenticationService.verifyUserRegistration(request)
        );
        assertFalse(mockUser.isEnabled());
    }



    @Test
    void changePassword_ShouldUpdatePassword_WhenOldPasswordIsCorrect() {
        UUID userId = UUID.randomUUID();
        UserData userData = new UserData(userId, "email", "pass", Set.of(), true);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("newPass");

        User mockUser = new User();
        mockUser.setPassword("encodedOldPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));


        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);

        when(passwordEncoder.matches("newPass", "encodedOldPass")).thenReturn(false);

        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        String result = authenticationService.changePassword(userData, request);

        assertEquals("Password changed successfully", result);
        assertEquals("encodedNewPass", mockUser.getPassword());
        verify(userRepository).save(mockUser);
    }
}
