package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.shift.CreateShift;
import demos.springdata.fitmanage.domain.dto.shift.ShiftResponse;
import demos.springdata.fitmanage.domain.entity.Employee;
import demos.springdata.fitmanage.domain.entity.Shift;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.repository.ShiftRepository;
import demos.springdata.fitmanage.service.EmployeeService;
import demos.springdata.fitmanage.service.ShiftService;
import demos.springdata.fitmanage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final EmployeeService employeeService;
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftServiceImpl.class);

    @Autowired
    public ShiftServiceImpl(ShiftRepository shiftRepository, EmployeeService employeeService, UserService userService) {
        this.shiftRepository = shiftRepository;
        this.employeeService = employeeService;
        this.userService = userService;
    }


    @Override
    public ShiftResponse createShift(CreateShift createRequest) {
        User user = userService.getCurrentUser();
        Tenant tenant = user.getTenant();

        Employee employee = employeeService.getEmployeeById(createRequest.getId(), tenant);

        Shift shift = new Shift()
                .setEmployee(employee)
                .setStartTime(createRequest.getStartTime())
                .setEndTime(createRequest.getEndTime())
                .setNotes(createRequest.getNotes())
                .setApproved(false);

        Shift savedShift = shiftRepository.save(shift);

        return ShiftResponse.builder()
                .id(savedShift.getId())
                .firstName(savedShift.getEmployee().getUser().getFirstName())
                .lastName(savedShift.getEmployee().getUser().getLastName())
                .startTime(savedShift.getStartTime())
                .endTime(savedShift.getEndTime())
                .role(savedShift.getEmployee().getEmployeeRole().name())
                .approved(savedShift.isApproved())
                .notes(savedShift.getNotes())
                .build();
    }

    @Override
    @Transactional
    public List<ShiftResponse> getShiftsForCurrentUser() {
        LOGGER.info("Get shifts information for current user");
        User user = userService.getCurrentUser();

        List<Shift> employeeShifts = shiftRepository.findByEmployee_User(user);

        return employeeShifts.stream().map(shift -> ShiftResponse.builder()
                .id(shift.getId())
                .firstName(shift.getEmployee().getUser().getFirstName())
                .lastName(shift.getEmployee().getUser().getLastName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .role(shift.getEmployee().getEmployeeRole().name())
                .approved(shift.isApproved())
                .notes(shift.getNotes())
                .build()).toList();
    }
}
