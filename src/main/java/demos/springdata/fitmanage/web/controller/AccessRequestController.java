package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponse;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.service.AccessRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/access-requests")
public class AccessRequestController {

    private final AccessRequestService accessRequestService;

    public AccessRequestController(AccessRequestService accessRequestService) {
        this.accessRequestService = accessRequestService;
    }

    @PostMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<MemberResponse>> requestAccess(
            @PathVariable Long tenantId,
            @Valid @RequestBody CreateUser requestDto) {
        MemberResponse response = accessRequestService.requestAccess(tenantId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PatchMapping("/{userId}/approve")
    public ResponseEntity<ApiResponse<MemberResponse>> approveAccess(
            @PathVariable Long userId) {
        MemberResponse response = accessRequestService.processAccessRequest(userId, true);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PatchMapping("/{userId}/reject")
    public ResponseEntity<ApiResponse<MemberResponse>> rejectAccess(
            @PathVariable Long userId) {
        MemberResponse response = accessRequestService.processAccessRequest(userId, false);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
