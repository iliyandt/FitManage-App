package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.employee.*;
import demos.springdata.fitmanage.domain.dto.users.UserLookup;
import demos.springdata.fitmanage.domain.entity.Employee;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.security.UserData;

import java.util.List;

public interface EmployeeService {
    EmployeeDataResponse createEmployee(CreateEmployee requestDto);
//    void deleteEmployee(Long id);
    void updateEmployee(Long id, UpdateEmployee update);
    List<EmployeeTable> getAllEmployees();
    List<EmployeeName> getEmployeesFullNames();
    Employee getEmployeeById(Long id, Tenant tenant);
    List<UserLookup> findEmployeesByEmployeeRole(String employeeRole);
}
