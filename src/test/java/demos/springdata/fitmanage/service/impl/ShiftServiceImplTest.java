package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.shift.CreateShift;
import demos.springdata.fitmanage.domain.dto.shift.ShiftResponse;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.repository.ShiftRepository;
import demos.springdata.fitmanage.service.EmployeeService;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceImplTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ShiftServiceImpl shiftService;

    private User currentUser;
    private User employeeUser;
    private Tenant tenant;
    private Employee employee;
    private Shift shift;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        employeeId = UUID.randomUUID();

        tenant = new Tenant();
        tenant.setId(tenantId);

        currentUser = new User();
        currentUser.setId(userId);
        currentUser.setTenant(tenant);

        employeeUser = new User();
        employeeUser.setId(UUID.randomUUID());
        employeeUser.setFirstName("John");
        employeeUser.setLastName("Worker");

        employee = new Employee();
        employee.setId(employeeId);
        employee.setUser(employeeUser);
        employee.setEmployeeRole(EmployeeRole.TRAINER);

        shift = new Shift();
        shift.setId(UUID.randomUUID());
        //shift.setEmployee(employee);
        shift.setStartTime(OffsetDateTime.now().plusHours(1));
        shift.setEndTime(OffsetDateTime.now().plusHours(9));
        shift.setApproved(true);
        shift.setNotes("Regular shift");
    }



    @Test
    void createShift_ShouldSaveAndReturnResponse() {

        CreateShift request = new CreateShift();
        request.setId(employeeId);
        request.setStartTime(OffsetDateTime.now().plusDays(1));
        request.setEndTime(OffsetDateTime.now().plusDays(1).plusHours(8));
        request.setNotes("New Shift Request");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(employeeService.getEmployeeById(employeeId, tenant)).thenReturn(employee);

        when(shiftRepository.save(any(Shift.class))).thenAnswer(invocation -> {
            Shift s = invocation.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        ShiftResponse response = shiftService.createShift(request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Worker", response.getLastName());
        assertEquals(request.getStartTime(), response.getStartTime());
        assertEquals("TRAINER", response.getRole());
        assertEquals("New Shift Request", response.getNotes());
        assertFalse(response.isApproved()); // Default value in Create method is false

        verify(shiftRepository).save(any(Shift.class));
    }

    @Test
    void getShiftsForCurrentUser_ShouldReturnListOfResponses() {

        when(userService.getCurrentUser()).thenReturn(employeeUser);

        //when(shiftRepository.findByEmployee_User(employeeUser)).thenReturn(List.of(shift));

        List<ShiftResponse> result = shiftService.getShiftsForCurrentUser();

        assertNotNull(result);
        assertEquals(1, result.size());

        ShiftResponse response = result.get(0);
        assertEquals("John", response.getFirstName());
        assertEquals(shift.getId(), response.getId());
        assertEquals(shift.isApproved(), response.isApproved());

        verify(userService).getCurrentUser();
       // verify(shiftRepository).findByEmployee_User(employeeUser);
    }

    @Test
    void getShiftsForCurrentUser_ShouldReturnEmptyList_WhenNoShiftsFound() {

        when(userService.getCurrentUser()).thenReturn(currentUser);
       // when(shiftRepository.findByEmployee_User(currentUser)).thenReturn(List.of());

        List<ShiftResponse> result = shiftService.getShiftsForCurrentUser();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}