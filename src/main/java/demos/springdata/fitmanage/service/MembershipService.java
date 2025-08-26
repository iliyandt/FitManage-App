package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.entity.Membership;

import java.util.Optional;
import java.util.Set;

public interface MembershipService {
    UserProfileDto initializeSubscription(Long memberId, MemberSubscriptionRequestDto requestDto);
    Membership checkIn(Membership membership);
   Membership getRequiredActiveMembership(Set<Membership> memberships);
    Optional<Membership> getActiveMembership(Set<Membership> memberships);
}
