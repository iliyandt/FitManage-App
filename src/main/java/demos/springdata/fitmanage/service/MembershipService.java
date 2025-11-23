package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.member.request.SubscriptionRequest;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MembershipService {
    UserResponse setupMembershipPlan(UUID memberId, SubscriptionRequest requestDto);

    Membership checkIn(Membership membership);

    Membership getRequiredActiveMembership(Set<Membership> memberships);

    void save(Membership membership);

    Double countByEmploymentForTenant(Employment employment);
    Double countBySubscriptionStatusForTenant(SubscriptionStatus status);
    Double countBySubscriptionPlanForTenant(SubscriptionPlan plan);

    Optional<Membership> getMembershipById(UUID membershipId);

}
