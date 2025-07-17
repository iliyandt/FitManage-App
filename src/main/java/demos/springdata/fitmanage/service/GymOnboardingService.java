package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberRequestDto;

import java.util.List;

public interface GymOnboardingService {
    void updateGymBasicInfo(String email, GymBasicInfoDto dto);
    void registerGymStaffMembers(String gymEmail, List<StaffMemberRequestDto> dtos);
}
