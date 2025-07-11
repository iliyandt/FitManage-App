package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberTableDto;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.StaffMemberService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        response.setConfig(tableHelper.buildTableConfig("staff-members"));
        response.setColumns(TableColumnBuilder.buildColumns(StaffMemberTableDto.class));
        response.setRows(tableHelper.buildRows(staff, tableHelper::buildRowMap));

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
