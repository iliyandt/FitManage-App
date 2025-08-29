package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.staff.StaffCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;

import java.util.List;

public interface EmployeeService {
    UserProfileDto createStaff(StaffCreateRequestDto requestDto);
    List<MemberTableDto> getAllEmployees();
}
