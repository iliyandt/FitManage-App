package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.request.*;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.auth.response.EmailResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.RegistrationResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.VerificationResponseDto;
import demos.springdata.fitmanage.domain.entity.RefreshToken;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.domain.dto.auth.response.LoginResponse;
import demos.springdata.fitmanage.service.AuthenticationService;
import demos.springdata.fitmanage.service.CustomUserDetailsService;
import demos.springdata.fitmanage.service.JwtService;
import demos.springdata.fitmanage.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<RegistrationResponseDto>> register(@Valid @RequestBody RegistrationRequestWrapper requestWrapper) {
        RegistrationResponseDto response = authenticationService.registerUser(requestWrapper.getUserDto(), requestWrapper.getTenantDto());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authenticationService.changePassword(request)));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationResponseDto>> verifyUser(@Valid @RequestBody VerificationRequestDto verificationRequestDto) {
        VerificationResponseDto response = authenticationService.verifyUserRegistration(verificationRequestDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{email}")
    public ResponseEntity<VerificationResponseDto> resendVerificationCode(@PathVariable String email) {
        return new ResponseEntity<>(authenticationService.resendUserVerificationCode(email), HttpStatus.CREATED);
    }

    @PostMapping("/validate_email")
    public ResponseEntity<ApiResponse<EmailResponseDto>> validateEmail(@Valid @RequestBody UserEmailRequestDto userEmailRequestDto) {
        EmailResponseDto response = authenticationService.checkIfEmailIsAvailable(userEmailRequestDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        UserDetails authenticatedUser = authenticationService.authenticateUser(loginRequestDto);

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
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(customUserDetailsService.loadUserByUsername(user.getEmail()));
                    return LoginResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequestDto.getToken())
                            .build();
                }).orElseThrow(() -> new FitManageAppException("Refresh token is not in the database", ApiErrorCode.NOT_FOUND));
    }

}
