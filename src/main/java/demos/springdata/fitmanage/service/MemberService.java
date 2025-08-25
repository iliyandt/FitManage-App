package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.users.UserProfileDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserBaseResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserCreateRequestDto;

import java.util.List;

public interface MemberService {
    UserProfileDto createMember(UserCreateRequestDto requestDto);
    void removeMember(Long memberId);
    UserProfileDto updateMemberDetails(Long memberId, MemberUpdateDto memberUpdateDto);
    UserProfileDto checkInMember(Long memberId);
    List<MemberTableDto> getAllMembersForTable();
    List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto memberFilterRequestDto);
    UserProfileDto findMember(MemberFilterRequestDto filter);
}
