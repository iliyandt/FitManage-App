package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeName;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeResponseDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTableDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;
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
@RequestMapping("api/v1/employees")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF') " +
        "and (@accessGuard.hasValidSubscription('GROWTH') " +
        "or @accessGuard.hasValidSubscription('PRO'))")
public class    EmployeeController {
    private final EmployeeService staffProfileService;
    private final TableHelper tableHelper;
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService staffProfileService, TableHelper tableHelper, EmployeeService employeeService) {
        this.staffProfileService = staffProfileService;
        this.tableHelper = tableHelper;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> createEmployee(@Valid @RequestBody EmployeeCreateRequestDto requestDto) {
        EmployeeResponseDto responseDto = staffProfileService.createEmployee(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TableResponseDto>> getEmployees() {
        TableResponseDto response = buildTableResponse(employeeService.getAllEmployees());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/names")
    public ResponseEntity<ApiResponse<List<EmployeeName>>> getEmployeesFullNames() {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeesFullNames()));
    }

    private TableResponseDto buildTableResponse(List<EmployeeTableDto> members) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/employees", EmployeeTableDto.class));
        response.setColumns(TableColumnBuilder.buildColumns(EmployeeTableDto.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));
        return response;
    }
}
