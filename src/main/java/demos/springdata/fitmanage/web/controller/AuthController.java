package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.GymLoginRequestDto;
import demos.springdata.fitmanage.domain.dto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@Valid @RequestBody GymRegistrationRequestDto gymDto) {
        authenticationService.registerGym(gymDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Gym registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody GymLoginRequestDto loginRequestDto) {
        authenticationService.loginGym(loginRequestDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully logged.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
