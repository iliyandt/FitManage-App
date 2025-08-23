package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserCreateRequestDto;

import java.util.List;

public interface MemberService {
    UserResponseDto createMember(UserCreateRequestDto requestDto);
    void removeMember(Long memberId);
    UserResponseDto updateMemberDetails(Long memberId, MemberUpdateDto memberUpdateDto);
    UserResponseDto checkInMember(Long tenantId, String input);
    List<MemberTableDto> getAllMembersForTable();
    List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto memberFilterRequestDto);
    UserResponseDto findMember(MemberFilterRequestDto filter);
}
