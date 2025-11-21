package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;

import java.util.List;

public interface MemberService {
    UserResponse create(CreateUser requestDto);
    void deleteMember(Long memberId);
    UserResponse updateMember(Long memberId, UserUpdate update);
    UserResponse checkInMember(Long memberId);
    List<MemberTableDto> findMembersTableView();
    List<MemberTableDto> getMembersByFilter(MemberFilter memberFilter);
    List<UserResponse> findMember(MemberFilter filter);
}
