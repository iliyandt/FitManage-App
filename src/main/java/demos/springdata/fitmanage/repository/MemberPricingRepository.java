package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.MemberPlanPrice;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberPricingRepository extends JpaRepository<MemberPlanPrice, Long> {
    Optional<MemberPlanPrice> findByGymIdAndSubscriptionPlan(Long gymId, SubscriptionPlan subscriptionPlan);
}
