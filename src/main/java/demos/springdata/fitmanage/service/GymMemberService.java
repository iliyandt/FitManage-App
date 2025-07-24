package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberUpdateRequestDto;

import java.util.List;

public interface GymMemberService {
    GymMemberResponseDto createAndSaveNewMember(GymMemberCreateRequestDto requestDto);
    List<GymMemberTableDto> getAllGymMembersForTable();
    GymMemberResponseDto updateMemberDetails(Long memberId, GymMemberUpdateRequestDto memberUpdateRequestDto);
    void removeGymMember(Long memberId);
}
