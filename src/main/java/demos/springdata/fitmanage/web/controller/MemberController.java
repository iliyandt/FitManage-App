package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponse;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdate;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.MemberService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api/v1/members")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
public class MemberController {
    private final MemberService memberService;
    private final TableHelper tableHelper;


    public MemberController(MemberService memberService, TableHelper tableHelper) {
        this.memberService = memberService;
        this.tableHelper = tableHelper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TableResponseDto>> getMembers(
            @ModelAttribute @Valid MemberFilter filter) {

        List<MemberTableDto> members = (!isFilterEmpty(filter))
                ? memberService.getMembersByFilter(filter)
                : memberService.findMembersTableView();

        TableResponseDto response = buildTableResponse(members);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> searchMember(@ModelAttribute @Valid MemberFilter filter) {
        List<MemberResponse> response = memberService.findMember(filter);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@Valid @RequestBody CreateUser requestDto) {
        MemberResponse responseDto = memberService.create(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @PostMapping("/{memberId}/check-in")
    public ResponseEntity<ApiResponse<MemberResponse>> checkInMember(
            @PathVariable Long memberId) {
        MemberResponse result = memberService.checkInMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(@PathVariable Long memberId, @Valid @RequestBody MemberUpdate memberUpdate) {
        MemberResponse userProfileDto = memberService.updateMember(memberId, memberUpdate);
        return ResponseEntity.ok(ApiResponse.success(userProfileDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteMember(@PathVariable Long id) {
        UserResponse response = memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private TableResponseDto buildTableResponse(List<MemberTableDto> members) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/members", MemberTableDto.class));
        response.setColumns(TableColumnBuilder.buildColumns(MemberTableDto.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));
        return response;
    }

    private boolean isFilterEmpty(MemberFilter filter) {
        return filter.getId() == null &&
                filter.getFirstName() == null &&
                filter.getLastName() == null &&
                filter.getGender() == null &&
                filter.getEmployment() == null &&
                filter.getBirthDate() == null &&
                filter.getEmail() == null &&
                filter.getSubscriptionStatus() == null &&
                filter.getSubscriptionPlan() == null &&
                filter.getPhone() == null;
    }
}