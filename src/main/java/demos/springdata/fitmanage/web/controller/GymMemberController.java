package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.ActionConfigDto;
import demos.springdata.fitmanage.domain.dto.ColumnConfigDto;
import demos.springdata.fitmanage.domain.dto.ConfigDto;
import demos.springdata.fitmanage.domain.dto.PaginationConfigDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.helper.GymMemberTableHelper;
import demos.springdata.fitmanage.service.GymMemberService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api/v1/gym-members")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class GymMemberController {
    private final GymMemberService gymMemberService;
    private final GymMemberTableHelper gymMemberTableHelper;

    public GymMemberController(GymMemberService gymMemberService, GymMemberTableHelper gymMemberTableHelper) {
        this.gymMemberService = gymMemberService;
        this.gymMemberTableHelper = gymMemberTableHelper;
    }


    @GetMapping("/table")
    public ResponseEntity<ApiResponse<GymMemberTableResponseDto>> getAllGymMembers() {
        List<GymMemberTableDto> members = gymMemberService.findAllGymMembers();

        GymMemberTableResponseDto response = new GymMemberTableResponseDto();
        response.setConfig(gymMemberTableHelper.buildTableConfig());
        response.setColumns(TableColumnBuilder.buildColumns(GymMemberTableDto.class));
        response.setRows(gymMemberTableHelper.buildRows(members));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> updateGymMember(@PathVariable Long memberId, @Valid @RequestBody GymMemberUpdateRequestDto memberUpdateRequestDto) {
        GymMemberResponseDto updatedGymMember = gymMemberService.updateGymMember(memberId, memberUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedGymMember));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteGymMember(@PathVariable Long memberId) {
        gymMemberService.deleteGymMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
