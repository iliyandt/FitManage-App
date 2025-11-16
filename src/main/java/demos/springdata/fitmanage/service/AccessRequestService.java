package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.request.CreateMemberRequest;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;

public interface AccessRequestService {
    MemberResponseDto requestAccess(Long tenantId, CreateMemberRequest createMemberRequest);
    MemberResponseDto processAccessRequest(Long membershipId, boolean approve);
}
