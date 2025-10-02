package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;

import java.util.List;

public interface MemberService {
    MemberResponseDto createMember(UserCreateRequestDto requestDto);
    void removeMember(Long memberId);
    MemberResponseDto updateMemberDetails(Long memberId, MemberUpdateDto memberUpdateDto);
    MemberResponseDto checkInMember(Long memberId);
    List<MemberTableDto> getAllMembersForTable();
    List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto memberFilterRequestDto);
    List<MemberResponseDto> findMember(MemberFilterRequestDto filter);
}
