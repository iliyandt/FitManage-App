package demos.springdata.fitmanage.service;
import demos.springdata.fitmanage.domain.dto.shift.ShiftCreateRequest;
import demos.springdata.fitmanage.domain.dto.shift.ShiftResponseDto;

import java.util.List;

public interface ShiftService {
    ShiftResponseDto createShift(ShiftCreateRequest createRequest);
    List<ShiftResponseDto> getShiftsForCurrentUser();
}
