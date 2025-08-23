package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserCreateRequestDto;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.MemberService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api/v1/users/members")
@PreAuthorize("hasAnyAuthority('FACILITY_ADMIN', 'FACILITY_STAFF')")
public class MemberController {
    private final MemberService memberService;
    private final TableHelper tableHelper;
    private final static Logger LOGGER = LoggerFactory.getLogger(MemberController.class);

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
    public ResponseEntity<ApiResponse<MemberResponseDto>> searchMember( @ModelAttribute @Valid MemberFilterRequestDto filter) {
        MemberResponseDto response = memberService.findMember(filter);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponseDto>> createMember(@Valid @RequestBody UserCreateRequestDto requestDto) {
        LOGGER.info("Received request to create member: {}", requestDto);
        MemberResponseDto responseDto = memberService.createMember(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @PostMapping("/{memberId}/check-in")
    public ResponseEntity<ApiResponse<MemberResponseDto>> checkInMember(
            @PathVariable Long memberId,
            @RequestParam String query) {

        MemberResponseDto result = memberService.checkInMember(memberId, query);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> updateMember(@PathVariable Long memberId, @Valid @RequestBody MemberUpdateDto memberUpdateDto) {
        MemberResponseDto updatedMember = memberService.updateMemberDetails(memberId, memberUpdateDto);
        return ResponseEntity.ok(ApiResponse.success(updatedMember));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long memberId) {
        memberService.removeMember(memberId);
        //todo: add response dto for delete
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    private TableResponseDto buildTableResponse(List<MemberTableDto> members) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/gym-members", MemberTableDto.class));
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