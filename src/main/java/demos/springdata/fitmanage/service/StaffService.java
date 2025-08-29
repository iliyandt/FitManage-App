package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.staff.StaffCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;

public interface StaffService {
    UserProfileDto createStaff(StaffCreateRequestDto requestDto);
}
