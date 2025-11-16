package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.employee.CreateEmployee;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeName;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeDataResponse;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTable;
import demos.springdata.fitmanage.domain.dto.users.UserLookup;
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
    private final RoleService roleService;
    private final UserService userService;
    private final UserValidationService userValidationService;
    private final UserPasswordService userPasswordService;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    public EmployeeServiceImpl
            (
                    EmployeeRepository employeeRepository,
                    TenantService tenantService,
                    RoleService roleService,
                    UserService userService,
                    UserValidationService userValidationService,
                    UserPasswordService userPasswordService
            ) {
        this.employeeRepository = employeeRepository;
        this.tenantService = tenantService;
        this.roleService = roleService;
        this.userService = userService;
        this.userValidationService = userValidationService;
        this.userPasswordService = userPasswordService;
    }

    @Transactional
    @Override
    public EmployeeDataResponse createEmployee(CreateEmployee requestDto) {
        User user = userService.getCurrentUser();
        Tenant tenant = tenantService.getTenantByEmail(user.getEmail());

        User member = buildEmployee(tenant, requestDto);
        userValidationService.validateTenantScopedCredentials(requestDto.getEmail(), requestDto.getPhone(), tenant.getId());
        userPasswordService.setupMemberInitialPassword(member);

        Employee employee = linkEmployeeToUser(tenant, member, requestDto);

        userService.save(member);
        employeeRepository.save(employee);

        LOGGER.info("Successfully added staff with ID {} to facility '{}'", member.getId(), tenant.getName());

        return EmployeeDataResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .username(member.getUsername())
                .email(member.getEmail())
                .gender(member.getGender())
                .roles(UserRoleHelper.extractRoleTypes(member))
                .birthDate(member.getBirthDate())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .phone(member.getPhone())
                .address(member.getAddress())
                .city(member.getCity())
                .employeeRole(employee.getEmployeeRole())
                .build();
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
                .map(empl -> new UserLookup(String.format("%s %s",empl.getFirstName(), empl.getLastName()),empl.getId().toString()))
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

    private Employee linkEmployeeToUser(Tenant tenant, User user, CreateEmployee requestDto) {
        Employee employee = new Employee()
                .setTenant(tenant)
                .setUser(user)
                .setEmployeeRole(requestDto.getEmployeeRole());

        user.getEmployees().add(employee);
        return employee;
    }

    private User buildEmployee(Tenant tenant, CreateEmployee requestDto) {

        User user = new User()
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setGender(requestDto.getGender())
                .setBirthDate(requestDto.getBirthDate())
                .setPhone(requestDto.getPhone())
                .setTenant(tenant)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setEnabled(true);

        Role role = roleService.findByName(RoleType.STAFF);
        user.getRoles().add(role);

        return user;
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
