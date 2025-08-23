package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserCreateRequestDto;

import java.util.List;

public interface MemberService {
    MemberResponseDto createMember(UserCreateRequestDto requestDto);
    void removeMember(Long memberId);
    MemberResponseDto updateMemberDetails(Long memberId, MemberUpdateDto memberUpdateDto);
    MemberResponseDto checkInMember(Long tenantId, String input);
    List<MemberTableDto> getAllMembersForTable();
    List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto memberFilterRequestDto);
    MemberResponseDto findMember(MemberFilterRequestDto filter);
}
