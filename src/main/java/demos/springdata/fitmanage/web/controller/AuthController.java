package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.request.*;
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

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody @Valid RegistrationRequestDto gymDto) {
        return new ResponseEntity<>(authenticationService.registerGym(gymDto), HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerificationResponseDto> verifyUser(@Valid @RequestBody VerificationRequestDto verificationRequestDto) {
        return new ResponseEntity<>(authenticationService.verifyUser(verificationRequestDto), HttpStatus.OK);
    }

    @PostMapping("/resend")
    public ResponseEntity<VerificationResponseDto> resendVerificationCode(@RequestParam String email) {
        return new ResponseEntity<>(authenticationService.resendVerificationCode(email), HttpStatus.CREATED);
    }


    @PostMapping(path = "/validate-email")
    public ResponseEntity<GymEmailResponseDto> validateEmail(@Valid @RequestBody GymEmailRequestDto gymEmailRequestDto) {
            return new ResponseEntity<>(authenticationService.validateEmail(gymEmailRequestDto).get(), HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        try {
            UserDetails authenticatedUser = authenticationService.authenticate(loginRequestDto);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getUsername());

            LoginResponse response = LoginResponse.builder()
                    .accessToken(jwtService.generateToken(authenticatedUser))
                    .refreshToken(refreshToken.getToken())
                    .build();
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "Wrong password. Please double-check and try again."));
        } catch (FitManageAppException e) {
            if (!loginRequestDto.isEnabled()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Map.of("message", "Verify email before login."));
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "An unexpected error occurred."));
            }

        }
    }


    @PostMapping("/refreshToken")
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
