package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.authenticationDto.GymEmailRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.LoginRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.domain.dto.authenticationDto.VerifyGymDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.responses.LoginResponse;
import demos.springdata.fitmanage.service.AuthenticationService;
import demos.springdata.fitmanage.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    public AuthController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@Valid @RequestBody GymRegistrationRequestDto gymDto) {
        LOGGER.info("Registration request received for username: {}", gymDto.getUsername());
        Gym registeredGym= authenticationService.registerGym(gymDto);
        LOGGER.info("Registration successful for username: {}", gymDto.getUsername());
        return ResponseEntity.ok(registeredGym);
    }

    @PostMapping(path = "/validate-email")
    public ResponseEntity<?> validateEmail(@Valid @RequestBody GymEmailRequestDto gymEmailRequestDto) {
        authenticationService.validateEmail(gymEmailRequestDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email is valid.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        UserDetails authenticatedUser = authenticationService.authenticate(loginRequestDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyGymDto verifyGymDto) {
        try {
            authenticationService.verifyUser(verifyGymDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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



}
