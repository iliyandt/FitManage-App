package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
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
            @ModelAttribute @Valid MemberFilterRequestDto filter) {

        List<MemberTableDto> members = (!isFilterEmpty(filter))
                ? memberService.getMembersByFilter(filter)
                : memberService.getAllMembersForTable();

        TableResponseDto response = buildTableResponse(members);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberResponseDto>>> searchMember(@ModelAttribute @Valid MemberFilterRequestDto filter) {
        List<MemberResponseDto> response = memberService.findMember(filter);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponseDto>> createMember(@Valid @RequestBody UserCreateRequestDto requestDto) {
        MemberResponseDto responseDto = memberService.createMember(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @PostMapping("/{memberId}/check-in")
    public ResponseEntity<ApiResponse<MemberResponseDto>> checkInMember(
            @PathVariable Long memberId) {

        MemberResponseDto result = memberService.checkInMember(memberId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> updateMember(@PathVariable Long memberId, @Valid @RequestBody MemberUpdateDto memberUpdateDto) {
        MemberResponseDto userProfileDto = memberService.updateMemberDetails(memberId, memberUpdateDto);
        return ResponseEntity.ok(ApiResponse.success(userProfileDto));
    }


    //todo: should i add response dto for delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        memberService.removeMember(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    private TableResponseDto buildTableResponse(List<MemberTableDto> members) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/members", MemberTableDto.class));
        response.setColumns(TableColumnBuilder.buildColumns(MemberTableDto.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));
        return response;
    }

    private boolean isFilterEmpty(MemberFilterRequestDto filter) {
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