package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserResponseDto;

import java.util.List;

public interface MemberService {
    MemberResponseDto createMember(UserCreateRequestDto requestDto);
    UserResponseDto removeMember(Long memberId);
    MemberResponseDto updateMemberDetails(Long memberId, MemberUpdateDto memberUpdateDto);
    MemberResponseDto checkInMember(Long memberId);
    List<MemberTableDto> getAllMembersForTable();
    List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto memberFilterRequestDto);
    List<MemberResponseDto> findMember(MemberFilterRequestDto filter);
}
