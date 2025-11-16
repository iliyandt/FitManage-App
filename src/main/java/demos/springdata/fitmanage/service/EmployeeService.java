package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.employee.CreateEmployee;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeName;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeDataResponse;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTable;
import demos.springdata.fitmanage.domain.dto.users.UserLookup;
import demos.springdata.fitmanage.domain.entity.Employee;
import demos.springdata.fitmanage.domain.entity.Tenant;

import java.util.List;

public interface EmployeeService {
    EmployeeDataResponse createEmployee(CreateEmployee requestDto);
    List<EmployeeTable> getAllEmployees();
    List<EmployeeName> getEmployeesFullNames();
    Employee getEmployeeById(Long id, Tenant tenant);
    List<UserLookup> findEmployeesByEmployeeRole(String employeeRole);
}
