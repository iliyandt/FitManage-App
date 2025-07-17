package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;

import java.util.List;
import java.util.Optional;

public interface GymService {
    List<GymSummaryDto> getAllGyms();
    Optional<GymSummaryDto> getGymByEmail(String email);
    GymMemberResponseDto registerNewMemberToGym(GymMemberCreateRequestDto requestDto);
    void updateGymBasicInfo(String email, GymBasicInfoDto dto);
}
