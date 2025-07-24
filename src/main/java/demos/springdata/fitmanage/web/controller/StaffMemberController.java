package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.team.request.StaffMemberRequestListDto;
import demos.springdata.fitmanage.domain.dto.team.response.RoleOptionDto;
import demos.springdata.fitmanage.domain.dto.team.response.StaffMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.team.response.StaffMemberTableDto;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.StaffMemberService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/staff-members")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class StaffMemberController {
    private final StaffMemberService staffMemberService;
    private final TableHelper tableHelper;

    public StaffMemberController(StaffMemberService staffMemberService, TableHelper tableHelper) {
        this.staffMemberService = staffMemberService;
        this.tableHelper = tableHelper;
    }

    @GetMapping("/table")
    public ResponseEntity<ApiResponse<TableResponseDto>> getAllStaffMembers() {
        List<StaffMemberTableDto> staff = staffMemberService.getStaffMembersTableData();

        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/staff-members", StaffMemberTableDto.class));
        response.setColumns(TableColumnBuilder.buildColumns(StaffMemberTableDto.class));
        response.setRows(tableHelper.buildRows(staff, tableHelper::buildRowMap));

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PostMapping("/staff")
    public ResponseEntity<ApiResponse<List<StaffMemberResponseDto>>> addStaff(@Valid @RequestBody StaffMemberRequestListDto staffListDto) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<StaffMemberResponseDto> createdStaff = staffMemberService.createStaffMembers(staffListDto.getStaff(), currentUser);
        return ResponseEntity.ok(ApiResponse.success(createdStaff));
    }

    @GetMapping("/staff/roles")
    public ResponseEntity<ApiResponse<List<RoleOptionDto>>> getStaffRoleOptionsForGym() {
        String gymEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RoleOptionDto> options = staffMemberService.getAllRoleOptionsForGym(gymEmail);
        return ResponseEntity.ok(ApiResponse.success(options));
    }
}
