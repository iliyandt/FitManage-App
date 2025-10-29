package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.users.UserLookupDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;
import demos.springdata.fitmanage.domain.dto.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/v1/user")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','ADMIN', 'MEMBER', 'STAFF')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //todo: logic for authentication should be in the service layer?
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> authenticatedUser(@AuthenticationPrincipal UserData userData) {
        UserResponseDto user = userService.getUserProfileByEmail(userData.getEmail());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@Valid @RequestBody UserUpdateDto dto) {
        UserResponseDto response = userService.updateProfile(dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/ids")
    public ResponseEntity<ApiResponse<List<UserLookupDto>>> getUsersForLookUp(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(ApiResponse.success(userService.findUsersWithIds(ids)));
    }

    @GetMapping("/users/roles")
    public ResponseEntity<ApiResponse<List<UserLookupDto>>> getUsersForLookUp(@RequestParam Set<String> roleNames) {
        return ResponseEntity.ok(ApiResponse.success(userService.findUsersWithRoles(roleNames)));
    }
}
