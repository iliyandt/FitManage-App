package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.team.StaffMemberTableDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StaffMemberService {
    List<StaffMemberTableDto> getStaffMembersTableData();
}
