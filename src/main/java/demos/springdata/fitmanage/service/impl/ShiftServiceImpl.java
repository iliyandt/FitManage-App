package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.shift.ShiftCreateRequest;
import demos.springdata.fitmanage.domain.dto.shift.ShiftResponseDto;
import demos.springdata.fitmanage.domain.entity.Employee;
import demos.springdata.fitmanage.domain.entity.Shift;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.repository.ShiftRepository;
import demos.springdata.fitmanage.service.EmployeeService;
import demos.springdata.fitmanage.service.ShiftService;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import org.modelmapper.ModelMapper;
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
    private final CurrentUserUtils currentUserUtils;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftServiceImpl.class);

    @Autowired
    public ShiftServiceImpl(ShiftRepository shiftRepository, EmployeeService employeeService, CurrentUserUtils currentUserUtils, ModelMapper modelMapper) {
        this.shiftRepository = shiftRepository;
        this.employeeService = employeeService;
        this.currentUserUtils = currentUserUtils;
        this.modelMapper = modelMapper;
    }


    @Override
    public ShiftResponseDto createShift(ShiftCreateRequest createRequest) {
        User user = currentUserUtils.getCurrentUser();
        Tenant tenant = user.getTenant();

        Employee employee = employeeService.getEmployeeById(createRequest.getId(), tenant);

        Shift shift = new Shift()
                .setEmployee(employee)
                .setStartTime(createRequest.getStartTime())
                .setEndTime(createRequest.getEndTime())
                .setNotes(createRequest.getNotes())
                .setApproved(false);

        Shift savedShift = shiftRepository.save(shift);
        ShiftResponseDto mappedShift = modelMapper.map(savedShift, ShiftResponseDto.class);
        return mappedShift.setFirstName(employee.getUser().getFirstName())
                .setLastName(employee.getUser().getLastName())
                .setRole(employee.getEmployeeRole().getDisplayName());
    }

    @Override
    @Transactional
    public List<ShiftResponseDto> getShiftsForCurrentUser() {
        LOGGER.info("Get shifts information for current user");
        User user = currentUserUtils.getCurrentUser();

        List<Shift> employeeShifts = shiftRepository.findByEmployee_User(user);


        return employeeShifts.stream().map(shift -> {
            ShiftResponseDto currentShift = modelMapper.map(shift, ShiftResponseDto.class);
            Employee employee = shift.getEmployee();

            currentShift.setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setRole(employee.getEmployeeRole().getDisplayName());

            return currentShift;
        }).toList();
    }
}
