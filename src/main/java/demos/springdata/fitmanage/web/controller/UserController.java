package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.users.UserLookup;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/v1/users")
@PreAuthorize("hasAnyAuthority('ADMINISTRATOR','ADMIN', 'MEMBER', 'STAFF')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<UserResponse>> authenticatedUser(@AuthenticationPrincipal UserData userData) {
        UserResponse user = userService.getUserProfileByEmail(userData.getEmail());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@Valid @RequestBody UserUpdate dto) {
        UserResponse response = userService.updateProfile(dto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lookup")
    public ResponseEntity<ApiResponse<List<UserLookup>>> getUsersForLookUp(@RequestParam Set<String> roleNames) {

        List<UserLookup> response = userService.findUsersWithRoles(roleNames).stream()
                .map(user -> new UserLookup(String.format("%s %s", user.getFirstName(), user.getLastName()),user.getId().toString()))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
