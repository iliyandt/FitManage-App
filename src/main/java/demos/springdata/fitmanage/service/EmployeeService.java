package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.employee.EmployeeName;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeResponseDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTableDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;
import demos.springdata.fitmanage.domain.entity.Employee;
import demos.springdata.fitmanage.domain.entity.Tenant;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDto createEmployee(UserCreateRequestDto requestDto);
    List<EmployeeTableDto> getAllEmployees();
    List<EmployeeName> getEmployeesFullNames();
    Employee getEmployeeById(Long id, Tenant tenant);
}
