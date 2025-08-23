package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserResponseDto;

public interface MembershipService {
    UserResponseDto initializeSubscription(Long memberId, MemberSubscriptionRequestDto requestDto);
}
