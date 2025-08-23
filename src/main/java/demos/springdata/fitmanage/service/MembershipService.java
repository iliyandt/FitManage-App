package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserResponseDto;
import demos.springdata.fitmanage.domain.entity.Membership;

import java.util.Optional;
import java.util.Set;

public interface MembershipService {
    UserResponseDto initializeSubscription(Long memberId, MemberSubscriptionRequestDto requestDto);
    Membership checkIn(Membership membership);
    Membership getActiveMembership(Set<Membership> memberships);
}
