package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

import java.util.Set;

public interface MembershipService {
    MemberResponseDto setupMembershipPlan(Long memberId, MemberSubscriptionRequestDto requestDto);

    Membership checkIn(Membership membership);

    Membership getRequiredActiveMembership(Set<Membership> memberships);

    void save(Membership membership);

    Double countByEmploymentForTenant(Employment employment);
    Double countBySubscriptionStatusForTenant(SubscriptionStatus status);
    Double countBySubscriptionPlanForTenant(SubscriptionPlan plan);
}
