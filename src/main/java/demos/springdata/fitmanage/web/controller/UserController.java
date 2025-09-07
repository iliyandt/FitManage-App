package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;
import demos.springdata.fitmanage.domain.dto.users.UserUpdateDto;
import demos.springdata.fitmanage.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/user")
@PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','FACILITY_ADMIN', 'FACILITY_MEMBER', 'FACILITY_STAFF')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //todo: logic for authentication should be in the service layer
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> authenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileDto user = userService.getUserProfileByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<String>> updateUser(@Valid @RequestBody UserUpdateDto dto) {
        userService.updateProfile(dto);
        return ResponseEntity.ok(ApiResponse.success("User details updated successfully."));
    }
}
