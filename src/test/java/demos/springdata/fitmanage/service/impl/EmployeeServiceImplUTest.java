package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.employee.*;
import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserLookup;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.EmployeeRepository;
import demos.springdata.fitmanage.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplUTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private TenantService tenantService;
    @Mock
    private UserService userService;
    @Mock
    private UserValidationService userValidationService;
    @Mock
    private UserPasswordService userPasswordService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void createEmployee_ShouldCreateAndLinkUserAndEmployee() {
        CreateUser request = new CreateUser();
        request.setEmail("staff@gym.bg");
        request.setPhone("0888123123");
        request.setEmployeeRole(EmployeeRole.TRAINER);

        UUID tenantId = UUID.randomUUID();
        Tenant mockTenant = new Tenant();
        mockTenant.setId(tenantId);
        mockTenant.setName("Fit Gym");

        User adminUser = new User();
        adminUser.setEmail("admin@gym.bg");

        adminUser.setEmployees(new ArrayList<>());

        User newEmployeeUser = new User();
        newEmployeeUser.setId(UUID.randomUUID());
        newEmployeeUser.setEmail(request.getEmail());

        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(tenantService.getTenantByEmail(adminUser.getEmail())).thenReturn(mockTenant);
        when(userMapper.toEmployeeUser(mockTenant, request)).thenReturn(newEmployeeUser);
        when(userMapper.toResponse(any(Employee.class), eq(newEmployeeUser))).thenReturn(new UserResponse());

        UserResponse response = employeeService.createEmployee(request);

        assertNotNull(response);

        verify(userValidationService).validateTenantScopedCredentials(request.getEmail(), request.getPhone(), tenantId);
        verify(userPasswordService).setupMemberInitialPassword(newEmployeeUser);

        verify(userService).save(newEmployeeUser);
        verify(employeeRepository).save(any(Employee.class));

        assertFalse(adminUser.getEmployees().isEmpty());
        assertEquals(EmployeeRole.TRAINER, adminUser.getEmployees().get(0).getEmployeeRole());
    }

    @Test
    void updateEmployee_ShouldUpdateOnlyNonNullFields() {

        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        Tenant tenant = new Tenant();
        tenant.setId(tenantId);

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("OldName");
        existingUser.setLastName("OldLast");
        existingUser.setTenant(tenant);

        Employee existingEmployee = new Employee();
        existingEmployee.setUser(existingUser);
        existingEmployee.setEmployeeRole(EmployeeRole.TRAINER);

        UpdateEmployee updateDto = new UpdateEmployee(
                "NewName",
                null,
                null,
                null,
                null,
                null,
                EmployeeRole.RECEPTIONIST
        );

        when(userService.findUserById(userId)).thenReturn(existingUser);
        when(employeeRepository.findByUser(existingUser)).thenReturn(existingEmployee);


        employeeService.updateEmployee(userId, updateDto);

        assertEquals("NewName", existingUser.getFirstName());
        assertEquals("OldLast", existingUser.getLastName());

        assertEquals(EmployeeRole.RECEPTIONIST, existingEmployee.getEmployeeRole());
        verify(employeeRepository).save(existingEmployee);

        verify(userService).save(existingUser);
        verifyNoInteractions(userValidationService);
    }

    @Test
    void updateEmployee_ShouldValidateAndUpdateEmail_WhenEmailIsProvided() {

        UUID userId = UUID.randomUUID();
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@test.com");
        existingUser.setTenant(tenant);

        UpdateEmployee updateDto = new UpdateEmployee(
                null, null, "new@test.com", Gender.MALE, null, "0888999999", null
        );

        when(userService.findUserById(userId)).thenReturn(existingUser);


        employeeService.updateEmployee(userId, updateDto);


        assertEquals("new@test.com", existingUser.getEmail());
        assertEquals("0888999999", existingUser.getPhone());


        verify(userValidationService).validateGlobalAndTenantScopedCredentials("new@test.com", "0888999999", tenant.getId());
        verify(userService).save(existingUser);
    }

    @Test
    void getAllEmployees_ShouldReturnMappedTableList() {

        User admin = new User();
        admin.setEmail("admin@test.com");

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        User staffUser = new User();
        staffUser.setId(UUID.randomUUID());
        staffUser.setFirstName("John");
        staffUser.setLastName("Doe");

        Employee employee = new Employee();
        employee.setUser(staffUser);
        employee.setEmployeeRole(EmployeeRole.TRAINER);

        when(userService.getCurrentUser()).thenReturn(admin);
        when(tenantService.getTenantByEmail(admin.getEmail())).thenReturn(tenant);
        when(employeeRepository.findAllByTenant_Id(tenant.getId())).thenReturn(List.of(employee));


        List<EmployeeTable> result = employeeService.getAllEmployees();


        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals(EmployeeRole.TRAINER, result.get(0).getEmployeeRole());
    }

    @Test
    void getEmployeeById_ShouldThrow_WhenNotFound() {

        UUID id = UUID.randomUUID();
        Tenant tenant = new Tenant();

        when(employeeRepository.findByIdAndTenant(id, tenant)).thenReturn(Optional.empty());


        DamilSoftException ex = assertThrows(DamilSoftException.class, () ->
                employeeService.getEmployeeById(id, tenant)
        );
        assertEquals("Employee not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void findEmployeesByEmployeeRole_ShouldFilterCorrectly() {

        String targetRoleStr = "TRAINER";

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setFirstName("Trainer");
        user1.setLastName("One");
        Employee emp1 = new Employee();
        emp1.setEmployeeRole(EmployeeRole.TRAINER);
        user1.setEmployees(List.of(emp1)); // ВАЖНО: Трябва да има елемент на индекс 0

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        Employee emp2 = new Employee();
        emp2.setEmployeeRole(EmployeeRole.RECEPTIONIST);
        user2.setEmployees(List.of(emp2));

        when(userService.findUsersWithRoles(Set.of("STAFF"))).thenReturn(List.of(user1, user2));

        List<UserLookup> result = employeeService.findEmployeesByEmployeeRole(targetRoleStr);

        assertEquals(1, result.size());
        assertEquals("Trainer One", result.get(0).title());
        assertEquals(user1.getId().toString(), result.get(0).value());
    }
}