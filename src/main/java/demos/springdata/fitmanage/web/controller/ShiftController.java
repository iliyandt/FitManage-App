package demos.springdata.fitmanage.web.controller;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.shift.ShiftCreateRequest;
import demos.springdata.fitmanage.domain.dto.shift.ShiftResponseDto;
import demos.springdata.fitmanage.service.ShiftService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/shifts")
public class ShiftController {
    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<ShiftResponseDto>> createShift(ShiftCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(shiftService.createShift(request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<ApiResponse<List<ShiftResponseDto>>> getMyShifts() {
        List<ShiftResponseDto> shifts = shiftService.getShiftsForCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(shifts));
    }
}
