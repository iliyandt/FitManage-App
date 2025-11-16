package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdate;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;

import java.util.List;

public interface MemberService {
    MemberResponseDto create(CreateUser requestDto);
    UserResponse deleteMember(Long memberId);
    MemberResponseDto updateMember(Long memberId, MemberUpdate memberUpdate);
    MemberResponseDto checkInMember(Long memberId);
    List<MemberTableDto> findMembersTableView();
    List<MemberTableDto> getMembersByFilter(MemberFilter memberFilter);
    List<MemberResponseDto> findMember(MemberFilter filter);
}
