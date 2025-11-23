package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.request.MemberFilter;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;

import java.util.List;
import java.util.UUID;

public interface MemberService {
    UserResponse create(CreateUser requestDto);
    void deleteMember(UUID memberId);
    UserResponse updateMember(UUID memberId, UserUpdate update);
    UserResponse checkInMember(UUID memberId);
    List<MemberTableDto> findMembersTableView();
    List<MemberTableDto> getMembersByFilter(MemberFilter memberFilter);
    List<UserResponse> findMember(MemberFilter filter);
}
