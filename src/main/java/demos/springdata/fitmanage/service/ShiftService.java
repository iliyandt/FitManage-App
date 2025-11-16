package demos.springdata.fitmanage.service;
import demos.springdata.fitmanage.domain.dto.shift.CreateShift;
import demos.springdata.fitmanage.domain.dto.shift.ShiftResponse;

import java.util.List;

public interface ShiftService {
    ShiftResponse createShift(CreateShift createRequest);
    List<ShiftResponse> getShiftsForCurrentUser();
}
