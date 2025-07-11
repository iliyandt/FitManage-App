package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.GymMemberService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api/v1/gym-members")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class GymMemberController {
    private final GymMemberService gymMemberService;
    private final TableHelper tableHelper;

    public GymMemberController(GymMemberService gymMemberService, TableHelper tableHelper) {
        this.gymMemberService = gymMemberService;
        this.tableHelper = tableHelper;
    }


    @GetMapping("/table")
    public ResponseEntity<ApiResponse<TableResponseDto>> getAllGymMembers() {
        List<GymMemberTableDto> members = gymMemberService.getAllGymMembersForTable();

        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/gym-members"));
        response.setColumns(TableColumnBuilder.buildColumns(GymMemberTableDto.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> updateGymMember(@PathVariable Long memberId, @Valid @RequestBody GymMemberUpdateRequestDto memberUpdateRequestDto) {
        GymMemberResponseDto updatedGymMember = gymMemberService.updateMemberDetails(memberId, memberUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedGymMember));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteGymMember(@PathVariable Long memberId) {
        gymMemberService.removeGymMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
