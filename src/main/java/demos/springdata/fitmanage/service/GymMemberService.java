package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;

import java.util.List;

public interface GymMemberService {
    GymMemberResponseDto createMemberForGym(Gym gym, GymMemberCreateRequestDto requestDto);
    List<GymMemberTableDto> findAllGymMembers();

    GymMemberResponseDto updateGymMember(Long memberId, GymMemberUpdateRequestDto memberUpdateRequestDto);
}
