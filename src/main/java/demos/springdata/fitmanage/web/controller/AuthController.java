package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.GymLoginRequestDto;
import demos.springdata.fitmanage.domain.dto.GymRegistrationRequestDto;
import demos.springdata.fitmanage.exception.FitManageAppException;
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
        try {
            authenticationService.registerGym(gymDto);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Gym registered successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch(FitManageAppException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            HttpStatus status = getHttpStatus(e);
            return new ResponseEntity<>(errorResponse, status);
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody GymLoginRequestDto loginRequestDto) {
        try {
            authenticationService.loginGym(loginRequestDto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully logged.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FitManageAppException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            HttpStatus status = getHttpStatus(e);
            return new ResponseEntity<>(errorResponse, status);
        }
    }


    //TODO: implementing a `@ControllerAdvice` global exception handler.
    private static HttpStatus getHttpStatus(FitManageAppException e) {
        return switch(e.getStatusCode()) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 404 -> HttpStatus.NOT_FOUND;
            case 409 -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
