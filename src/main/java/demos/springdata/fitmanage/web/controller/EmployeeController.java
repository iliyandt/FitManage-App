package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTableDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.EmployeeService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users/employee")
@PreAuthorize("hasAuthority('FACILITY_ADMIN')")
public class EmployeeController {
    private final EmployeeService staffProfileService;
    private final TableHelper tableHelper;
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService staffProfileService, TableHelper tableHelper, EmployeeService employeeService) {
        this.staffProfileService = staffProfileService;
        this.tableHelper = tableHelper;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserProfileDto>> createEmployee(@Valid @RequestBody EmployeeCreateRequestDto requestDto) {
        UserProfileDto responseDto = staffProfileService.createEmployee(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TableResponseDto>> getEmployees() {
        TableResponseDto response = buildTableResponse(employeeService.getAllEmployees());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private TableResponseDto buildTableResponse(List<EmployeeTableDto> members) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/users/employee", EmployeeTableDto.class));
        response.setColumns(TableColumnBuilder.buildColumns(EmployeeTableDto.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));
        return response;
    }

    private boolean isFilterEmpty(MemberFilterRequestDto filter) {
        return filter.getId() == null &&
                filter.getQrToken() == null &&
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
