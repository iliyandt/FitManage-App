package demos.springdata.fitmanage.web.controller;
import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.RegisterResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponse;
import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.domain.dto.auth.response.LoginResponse;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.AuthenticationService;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import demos.springdata.fitmanage.service.JwtService;
import demos.springdata.fitmanage.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;


    public AuthController(JwtService jwtService, AuthenticationService authenticationService, RefreshTokenService refreshTokenService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping( "/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegistrationRequestWrapper requestWrapper) {
        RegisterResponse response = authenticationService.registerUser(requestWrapper.userDto(), requestWrapper.tenantRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@AuthenticationPrincipal UserData userData, @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authenticationService.changePassword(userData, request)));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyUser(@Valid @RequestBody VerificationRequest verificationRequest) {
        VerificationResponse response = authenticationService.verifyUserRegistration(verificationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping("verification-code/{email}")
    public ResponseEntity<ApiResponse<VerificationResponse>> resendVerificationCode(@PathVariable String email) {
        VerificationResponse dto = authenticationService.resendUserVerificationCode(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dto));
    }

    @PostMapping("/validate_email")
    public ResponseEntity<ApiResponse<EmailResponse>> validateEmail(@Valid @RequestBody EmailValidationRequest emailValidationRequest) {
        EmailResponse response = authenticationService.findUserEmail(emailValidationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {

        UserDetails authenticatedUser = authenticationService.authenticateUser(loginRequest);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getUsername());

        LoginResponse response = LoginResponse.builder()
                .accessToken(jwtService.generateToken(authenticatedUser))
                .refreshToken(refreshToken.getToken())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }


    @PostMapping("/refresh_token")
    public LoginResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.token())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));
                    return LoginResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.token())
                            .build();
                }).orElseThrow(() -> new DamilSoftException("Refresh token is not in the database", HttpStatus.NOT_FOUND));
    }

}
