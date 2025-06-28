package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.dto.gym.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gym.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface GymService {
    List<GymSummaryDto> getAllGyms();
    Optional<GymSummaryDto> getGymByEmail(String email);
    GymMemberResponseDto addGymMemberToGym(GymMemberCreateRequestDto requestDto);
}
