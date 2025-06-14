package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.*;
import demos.springdata.fitmanage.domain.entity.Gym;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequestDto gymDto) {
        if (!gymDto.isPasswordConfirmed()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "Passwords do not match"));
        }

        try {
            Gym registeredGym = authenticationService.registerGym(gymDto);
            LOGGER.info("Registration successful for username: {}", gymDto.getUsername());
            return ResponseEntity.ok(registeredGym);
        } catch (FitManageAppException e) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "Email is already taken"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred."));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyGymDto verifyGymDto) {
        try {
            authenticationService.verifyUser(verifyGymDto);
            return ResponseEntity.ok("Account verified successfully.");
        } catch (FitManageAppException e) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Invalid code."));
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping(path = "/validate-email")
    public ResponseEntity<?> validateEmail(@Valid @RequestBody GymEmailRequestDto gymEmailRequestDto) {
        try {
            authenticationService.validateEmail(gymEmailRequestDto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email is valid.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FitManageAppException e) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", "Account with this email does not exists."));
        }

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
        public LoginResponse refreshToken (@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){

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
