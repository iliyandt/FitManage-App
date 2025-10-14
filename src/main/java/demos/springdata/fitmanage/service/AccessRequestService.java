package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.request.MemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;

public interface AccessRequestService {
    MemberResponseDto requestAccess(Long tenantId, MemberCreateRequestDto memberCreateRequestDto);
    MemberResponseDto processAccessRequest(Long membershipId, boolean approve);
}
