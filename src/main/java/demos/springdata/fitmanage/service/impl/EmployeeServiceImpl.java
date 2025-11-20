package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.employee.*;
import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserLookup;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.EmployeeRepository;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final TenantService tenantService;
    private final UserService userService;
    private final UserValidationService userValidationService;
    private final UserPasswordService userPasswordService;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final UserMapper userMapper;

    @Autowired
    public EmployeeServiceImpl
            (
                    EmployeeRepository employeeRepository,
                    TenantService tenantService,
                    UserService userService,
                    UserValidationService userValidationService,
                    UserPasswordService userPasswordService,
                    UserMapper userMapper) {
        this.employeeRepository = employeeRepository;
        this.tenantService = tenantService;
        this.userService = userService;
        this.userValidationService = userValidationService;
        this.userPasswordService = userPasswordService;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserResponse createEmployee(CreateUser request) {
        User user = userService.getCurrentUser();
        Tenant tenant = tenantService.getTenantByEmail(user.getEmail());

        User employeeUser = userMapper.toEmployeeUser(tenant, request);

        userValidationService.validateTenantScopedCredentials(request.getEmail(), request.getPhone(), tenant.getId());
        userPasswordService.setupMemberInitialPassword(employeeUser);

        Employee employee = new Employee()
                .setTenant(tenant)
                .setUser(employeeUser)
                .setEmployeeRole(request.getEmployeeDetails().getEmployeeRole());

        user.getEmployees().add(employee);

        userService.save(employeeUser);
        employeeRepository.save(employee);

        LOGGER.info("Successfully added staff with ID {} to facility '{}'", employeeUser.getId(), tenant.getName());

        return userMapper.toResponse(employee, employeeUser);
    }

    @Transactional
    @Override
    public void updateEmployee(Long id, UpdateEmployee updateDto) {

        User employeeUser = userService.findUserById(id);

        if (updateDto.firstName() != null) {
            employeeUser.setFirstName(updateDto.firstName());
        }

        if (updateDto.lastName() != null) {
            employeeUser.setLastName(updateDto.lastName());
        }

        if (updateDto.email() != null || updateDto.phone() != null) {
            userValidationService.validateGlobalAndTenantScopedCredentials(updateDto.email(), updateDto.phone(), employeeUser.getTenant().getId());
            employeeUser.setEmail(updateDto.email());
            employeeUser.setPhone(updateDto.phone());
        }

        if (updateDto.gender() != null) {
            employeeUser.setGender(updateDto.gender());
        }

        if (updateDto.birthDate() != null) {
            employeeUser.setBirthDate(updateDto.birthDate());
        }

        if (updateDto.employeeRole() != null) {
            Employee employee = employeeRepository.findByUser(employeeUser);
            employee.setEmployeeRole(updateDto.employeeRole());
            employeeRepository.save(employee);
        }

        userService.save(employeeUser);
    }

    @Transactional
    @Override
    public List<EmployeeTable> getAllEmployees() {
        LOGGER.info("Fetching all members..");
        User user = userService.getCurrentUser();
        Tenant tenant = tenantService.getTenantByEmail(user.getEmail());

        LOGGER.info("Tenant ID: {}", tenant.getId());

        List<Employee> employees = employeeRepository.findAllByTenant_Id(tenant.getId());
        LOGGER.info("Found {} employees", employees.size());

        return employees.stream()
                .map(this::buildTableResponse)
                .toList();
    }

    @Override
    public List<EmployeeName> getEmployeesFullNames() {
        User user = userService.getCurrentUser();

        return UserRoleHelper.isFacilityAdmin(user)
                ? getAllEmployeesForTenant(user.getTenant().getId())
                : getSingleEmployeeForUser(user.getTenant().getId(), user.getId());
    }

    @Override
    public Employee getEmployeeById(Long id, Tenant tenant) {
        return employeeRepository.findByIdAndTenant(id, tenant).orElseThrow(() -> new DamilSoftException("Employee not found", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public List<UserLookup> findEmployeesByEmployeeRole(String employeeRole) {
        List<User> staffUsers = userService.findUsersWithRoles(Set.of("STAFF"));
        EmployeeRole targetRole = EmployeeRole.valueOf(employeeRole);

        List<User> employeesWithRole = staffUsers.stream()
                .filter(employee -> employee.getEmployees().get(0).getEmployeeRole().equals(targetRole))
                .toList();

        return employeesWithRole.stream()
                .map(empl -> new UserLookup(String.format("%s %s", empl.getFirstName(), empl.getLastName()), empl.getId().toString()))
                .toList();
    }

    private List<EmployeeName> getAllEmployeesForTenant(Long tenantId) {
        return employeeRepository.findAllByTenant_Id(tenantId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    private List<EmployeeName> getSingleEmployeeForUser(Long tenantId, Long userId) {
        Employee employee = employeeRepository.findByTenant_IdAndUser_Id(tenantId, userId);
        return List.of(buildResponse(employee));
    }

    private EmployeeName buildResponse(Employee employee) {
        return new EmployeeName(
                employee.getId(),
                employee.getUser().getFirstName() + " " + employee.getUser().getLastName()
        );
    }

    private EmployeeTable buildTableResponse(Employee employee) {
        User user = employee.getUser();

        return EmployeeTable.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .gender(user.getGender())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .employeeRole(employee.getEmployeeRole())
                .build();
    }
}
