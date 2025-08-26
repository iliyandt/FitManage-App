package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.entity.Tenant;

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
