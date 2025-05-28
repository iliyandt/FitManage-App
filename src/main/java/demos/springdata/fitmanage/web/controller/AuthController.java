package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.GymLoginRequestDto;
import demos.springdata.fitmanage.domain.dto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<?> register(@RequestBody GymRegistrationRequestDto gymDto) {
        try {
            authenticationService.registerGym(gymDto);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Gym registered successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch(FitManageAppException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(201));

        }
    }


    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody GymLoginRequestDto loginRequestDto) {
        try {
            authenticationService.loginGym(loginRequestDto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully logged.");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (FitManageAppException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(201));
        }
    }
}
