package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gym.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.entity.Gym;

public interface GymMemberService {
    GymMemberResponseDto createMemberForGym(Gym gym, GymMemberCreateRequestDto requestDto);
}
