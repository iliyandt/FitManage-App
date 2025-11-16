package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;

public interface AccessRequestService {
    MemberResponseDto requestAccess(Long tenantId, CreateUser createMemberRequest);
    MemberResponseDto processAccessRequest(Long membershipId, boolean approve);
}
