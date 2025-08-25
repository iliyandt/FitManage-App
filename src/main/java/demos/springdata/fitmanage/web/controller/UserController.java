package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserBaseResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserProfileDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserUpdateDto;
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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> authenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileDto user = userService.getUserProfileByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    //todo: what is if the given id is from another user here? Can i change the details from another user without permission?
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
        userService.updateProfile(id, dto);
        return ResponseEntity.ok(ApiResponse.success("User details updated successfully."));
    }
}
