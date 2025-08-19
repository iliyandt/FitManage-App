package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserCreateRequestDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    MemberResponseDto createMember(UserCreateRequestDto requestDto);
    void removeMember(Long memberId);
    MemberResponseDto updateMemberDetails(Long memberId, MemberUpdateRequestDto memberUpdateRequestDto);
    MemberResponseDto checkInMember(Long tenantId, String input);
    List<MemberTableDto> getAllMembersForTable();
    List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto memberFilterRequestDto);
    Optional<MemberResponseDto> getMemberById(String input, Long gymId);
}
