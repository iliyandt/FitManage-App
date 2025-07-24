package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.team.response.RoleOptionDto;
import demos.springdata.fitmanage.domain.dto.team.request.StaffMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.team.response.StaffMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.team.response.StaffMemberTableDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StaffMemberService {
    List<StaffMemberTableDto> getStaffMembersTableData();
    List<StaffMemberResponseDto> createStaffMembers(List<StaffMemberCreateRequestDto> requests, String gymEmail);
    List<RoleOptionDto> getAllRoleOptionsForGym(String gymEmail);

}
