package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.GymEmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponseDto;
import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.responses.LoginResponse;
import demos.springdata.fitmanage.service.AuthenticationService;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import demos.springdata.fitmanage.service.JwtService;
import demos.springdata.fitmanage.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    
    
    public AuthController(JwtService jwtService, AuthenticationService authenticationService, RefreshTokenService refreshTokenService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse<RegistrationResponseDto>> register(@Valid @RequestBody RegistrationRequestDto gymDto) {
        try {
            RegistrationResponseDto response = authenticationService.registerGym(gymDto);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (AuthenticationException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Invalid credentials", "AUTH_INVALID"));
        }

    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationResponseDto>> verifyUser(@Valid @RequestBody VerificationRequestDto verificationRequestDto) {
        try {
            VerificationResponseDto response = authenticationService.verifyUser(verificationRequestDto);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (AuthenticationException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("Invalid verification code", "CODE_INVALID"));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<VerificationResponseDto> resendVerificationCode(@RequestParam String email) {
        return new ResponseEntity<>(authenticationService.resendVerificationCode(email), HttpStatus.CREATED);
    }


    @PostMapping(path = "/validate_email")
    public ResponseEntity<ApiResponse<GymEmailResponseDto>> validateEmail(@Valid @RequestBody GymEmailRequestDto gymEmailRequestDto) {
        try {
            GymEmailResponseDto response = authenticationService.validateEmail(gymEmailRequestDto).get();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (FitManageAppException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("Invalid email", "EMAIL_INVALID"));
        }

    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        UserDetails authenticatedUser = authenticationService.login(loginRequestDto);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getUsername());

        LoginResponse response = LoginResponse.builder()
                .accessToken(jwtService.generateToken(authenticatedUser))
                .refreshToken(refreshToken.getToken())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/refresh_token")
    public LoginResponse refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return refreshTokenService.findByToken(refreshTokenRequestDto.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getGym)
                .map(gym -> {
                    String accessToken = jwtService.generateToken(customUserDetailsService.loadUserByUsername(gym.getEmail()));
                    return LoginResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequestDto.getToken())
                            .build();
                }).orElseThrow(() -> new FitManageAppException("Refresh token is not in the database", ApiErrorCode.NOT_FOUND));
    }

}
