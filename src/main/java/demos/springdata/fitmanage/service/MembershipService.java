package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberResponseDto;

public interface MembershipService {
    MemberResponseDto initializeSubscription(Long memberId, MemberSubscriptionRequestDto requestDto);
}
