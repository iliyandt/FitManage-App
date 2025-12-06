package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.employee.*;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserLookup;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.entity.Employee;
import demos.springdata.fitmanage.domain.entity.Tenant;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    UserResponse createEmployee(CreateUser requestDto);
    UserResponse updateEmployee(UUID id, UpdateEmployee update);
    List<EmployeeTableDto> getAllEmployees();
    List<EmployeeName> getEmployeesFullNames();
    Employee getEmployeeById(UUID id, Tenant tenant);
    List<UserLookup> findEmployeesByEmployeeRole(String employeeRole);
}
