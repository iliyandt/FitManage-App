package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponse;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdate;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;

import java.util.List;

public interface MemberService {
    MemberResponse create(CreateUser requestDto);
    UserResponse deleteMember(Long memberId);
    MemberResponse updateMember(Long memberId, MemberUpdate memberUpdate);
    MemberResponse checkInMember(Long memberId);
    List<MemberTableDto> findMembersTableView();
    List<MemberTableDto> getMembersByFilter(MemberFilter memberFilter);
    List<MemberResponse> findMember(MemberFilter filter);
}
