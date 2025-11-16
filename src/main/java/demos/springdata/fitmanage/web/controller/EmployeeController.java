package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.employee.*;
import demos.springdata.fitmanage.domain.dto.users.UserLookup;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.EmployeeService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employees")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF') " +
        "and (@accessGuard.hasValidSubscription('GROWTH') " +
        "or @accessGuard.hasValidSubscription('PRO'))")
public class EmployeeController { ;
    private final TableHelper tableHelper;
    private final EmployeeService employeeService;

    public EmployeeController(TableHelper tableHelper, EmployeeService employeeService) {

        this.tableHelper = tableHelper;
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDataResponse>> createEmployee(@Valid @RequestBody CreateEmployee requestDto) {
        EmployeeDataResponse responseDto = employeeService.createEmployee(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TableResponseDto>> getEmployees() {
        TableResponseDto response = buildTableResponse(employeeService.getAllEmployees());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, UpdateEmployee update) {
        employeeService.updateEmployee(id, update);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Successfully updated."));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
//        employeeService.deleteEmployee(user, id);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Successfully deleted."));
//    }

    @GetMapping("/names")
    public ResponseEntity<ApiResponse<List<EmployeeName>>> getEmployeesFullNames() {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeesFullNames()));
    }

    @GetMapping("/role")
    public ResponseEntity<ApiResponse<List<UserLookup>>> getEmployeesWithRole() {
        List<UserLookup> employeesByEmployeeRole = employeeService.findEmployeesByEmployeeRole("TRAINER");

        return ResponseEntity.ok(ApiResponse.success(employeesByEmployeeRole));
    }

    private TableResponseDto buildTableResponse(List<EmployeeTable> members) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/employees", EmployeeTable.class));
        response.setColumns(TableColumnBuilder.buildColumns(EmployeeTable.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));
        return response;
    }
}
