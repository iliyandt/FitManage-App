package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberRequestDto;

import java.util.List;

public interface GymOnboardingService {
    void updateBasicInfo(String email, GymBasicInfoDto dto);
    void addTeamMembers(String gymEmail, List<StaffMemberRequestDto> dtos);
}
