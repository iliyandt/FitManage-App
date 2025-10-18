package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.service.AccessRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAnyAuthority('FACILITY_ADMIN', 'FACILITY_STAFF')")
@RestController
@RequestMapping("/api/v1/admin/access-requests")
public class AccessRequestAdminController {

    private final AccessRequestService accessRequestService;

    public AccessRequestAdminController(AccessRequestService accessRequestService) {
        this.accessRequestService = accessRequestService;
    }

    @PatchMapping("/{userId}/approve")
    public ResponseEntity<ApiResponse<MemberResponseDto>> approveAccess(
            @PathVariable Long userId) {

        MemberResponseDto response = accessRequestService.processAccessRequest(userId, true);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{userId}/reject")
    public ResponseEntity<ApiResponse<MemberResponseDto>> rejectAccess(
            @PathVariable Long userId) {

        MemberResponseDto response = accessRequestService.processAccessRequest(userId, false);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
