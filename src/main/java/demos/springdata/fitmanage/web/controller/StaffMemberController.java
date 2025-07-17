package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.common.EnumOption;
import demos.springdata.fitmanage.domain.dto.common.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.team.RoleOptionDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberRequestDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberTableDto;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
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
        response.setConfig(tableHelper.buildTableConfig("staff-members"));
        response.setColumns(TableColumnBuilder.buildColumns(StaffMemberTableDto.class));
        response.setRows(tableHelper.buildRows(staff, tableHelper::buildRowMap));

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PostMapping("/staff")
    public ResponseEntity<ApiResponse<List<StaffMemberResponseDto>>> addStaff(@Valid @RequestBody List<StaffMemberRequestDto> staffDtos) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            List<StaffMemberResponseDto> createdStaff = staffMemberService.createStaffMembers(staffDtos, currentUser);
            return ResponseEntity.ok(ApiResponse.success(createdStaff));
        } catch (FitManageAppException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.failure(e.getMessage(), e.getErrorCode().name())
            );
        } catch (MultipleValidationException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.failure("Validation failed", "VALIDATION_ERROR", e.getErrors())
            );
        }

    }

    @GetMapping("/staff/roles")
    public ResponseEntity<ApiResponse<List<RoleOptionDto>>> getStaffRoleOptionsForGym() {
        String gymEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RoleOptionDto> options = staffMemberService.getAllRoleOptionsForGym(gymEmail);
        return ResponseEntity.ok(ApiResponse.success(options));

    }
}
