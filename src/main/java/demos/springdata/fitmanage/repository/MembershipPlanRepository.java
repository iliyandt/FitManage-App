package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, UUID> {
    List<MembershipPlan> getMembershipPlansByTenant(Tenant tenant);
    MembershipPlan getMembershipPlanById(UUID id);

    @Query("""
        SELECT CASE :employment
            WHEN 'STUDENT' THEN m.studentPrice
            WHEN 'SENIOR' THEN m.seniorPrice
            WHEN 'HANDICAP' THEN m.handicapPrice
            ELSE m.price
        END
        FROM MembershipPlan m
        WHERE m.tenant = :tenant AND m.subscriptionPlan = :subscriptionPlan
    """)
    BigDecimal findPriceByTenantAndSubscriptionPlanAndEmployment(
            @Param("tenant") Tenant tenant,
            @Param("subscriptionPlan") SubscriptionPlan subscriptionPlan,
            @Param("employment") String employment
    );
}
