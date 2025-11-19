package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberDetails;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;

public interface AccessRequestService {
    UserResponse requestAccess(Long tenantId, CreateUser createMemberRequest);
    UserResponse processAccessRequest(Long membershipId, boolean approve);
}
