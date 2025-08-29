package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.staff.StaffCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;
import demos.springdata.fitmanage.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users/staff")
public class StaffController {
    private final StaffService staffProfileService;

    public StaffController(StaffService staffProfileService) {
        this.staffProfileService = staffProfileService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserProfileDto>> createStaff(@Valid @RequestBody StaffCreateRequestDto requestDto) {
        UserProfileDto responseDto = staffProfileService.createStaff(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }
}
