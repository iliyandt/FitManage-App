package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.team.RoleOptionDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberTableDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StaffMemberService {
    List<StaffMemberTableDto> getStaffMembersTableData();
    List<StaffMemberResponseDto> createStaffMembers(List<StaffMemberCreateRequestDto> requests, String gymEmail);
    List<RoleOptionDto> getAllRoleOptionsForGym(String gymEmail);

}
