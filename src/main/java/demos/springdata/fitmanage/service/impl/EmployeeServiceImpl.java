package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.employee.EmployeeName;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeResponseDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTableDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.*;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.EmployeeRepository;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    public EmployeeServiceImpl
            (
                    EmployeeRepository employeeRepository,
                    TenantService tenantService,
                    RoleService roleService,
                    UserService userService,
                    UserValidationService userValidationService,
                    UserPasswordService userPasswordService,
                    ModelMapper modelMapper
            ) {
        this.employeeRepository = employeeRepository;
        this.tenantService = tenantService;
        this.roleService = roleService;
        this.userService = userService;
        this.userValidationService = userValidationService;
        this.userPasswordService = userPasswordService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public EmployeeResponseDto createEmployee(UserCreateRequestDto requestDto) {
        User user = userService.getCurrentUser();
        Tenant tenant = tenantService.getTenantByEmail(user.getEmail());

        User member = buildEmployee(tenant, requestDto);
        userValidationService.validateTenantScopedCredentials(requestDto.getEmail(), requestDto.getPhone(), tenant.getId());
        userPasswordService.setupMemberInitialPassword(member);

        Employee employee = createAndLinkStaffProfileToUser(tenant, member, requestDto);

        userService.save(member);
        employeeRepository.save(employee);

        LOGGER.info("Successfully added staff with ID {} to facility '{}'", member.getId(), tenant.getName());

        EmployeeResponseDto mappedStaff = modelMapper.map(member, EmployeeResponseDto.class);
        mappedStaff.setRoles(UserRoleHelper.extractRoleTypes(member));
        modelMapper.map(employee, mappedStaff);
        mappedStaff.setEmployeeRole(requestDto.getEmployeeRole());

        return mappedStaff;
    }

    @Transactional
    @Override
    public List<EmployeeTableDto> getAllEmployees() {
        LOGGER.info("Fetching all members..");
        User user = userService.getCurrentUser();
        Tenant tenant = tenantService.getTenantByEmail(user.getEmail());

        LOGGER.info("Tenant ID: {}", tenant.getId());

        List<Employee> employees = employeeRepository.findAllByTenant_Id(tenant.getId());
        LOGGER.info("Found {} employees", employees.size());

        return employees.stream()
                .map(this::mapEmployeeToEmployeeTableDto)
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
        return employeeRepository.findByIdAndTenant(id, tenant).orElseThrow(() -> new FitManageAppException("Employee not found", ApiErrorCode.NOT_FOUND));
    }

    private List<EmployeeName> getAllEmployeesForTenant(Long tenantId) {
        return employeeRepository.findAllByTenant_Id(tenantId)
                .stream()
                .map(this::mapToEmployeeName)
                .toList();
    }

    private List<EmployeeName> getSingleEmployeeForUser(Long tenantId, Long userId) {
        Employee employee = employeeRepository.findByTenant_IdAndUser_Id(tenantId, userId);
        return List.of(mapToEmployeeName(employee));
    }

    private EmployeeName mapToEmployeeName(Employee employee) {
        return new EmployeeName()
                .setId(employee.getId())
                .setName(employee.getUser().getFirstName() + " " + employee.getUser().getLastName());
    }

    private Employee createAndLinkStaffProfileToUser(Tenant tenant, User user, UserCreateRequestDto requestDto) {
        Employee employee = new Employee()
                .setTenant(tenant)
                .setUser(user)
                .setEmployeeRole(requestDto.getEmployeeRole());

        user.getEmployees().add(employee);
        return employee;
    }

    private User buildEmployee(Tenant tenant, UserCreateRequestDto requestDto) {

        User user = modelMapper.map(requestDto, User.class);
        user.setTenant(tenant)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setEnabled(true);

        Role role = roleService.findByName(RoleType.STAFF);
        user.getRoles().add(role);

        return user;
    }

    private EmployeeTableDto mapEmployeeToEmployeeTableDto(Employee employee) {
        User user = employee.getUser();
        EmployeeTableDto dto = modelMapper.map(user, EmployeeTableDto.class);
        dto.setEmployeeRole(employee.getEmployeeRole());
        return dto;
    }
}
