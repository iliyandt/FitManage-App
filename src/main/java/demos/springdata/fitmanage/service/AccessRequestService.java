package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponse;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;

public interface AccessRequestService {
    MemberResponse requestAccess(Long tenantId, CreateUser createMemberRequest);
    MemberResponse processAccessRequest(Long membershipId, boolean approve);
}
