package demos.springdata.fitmanage.repository;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByUserAndTenant(User user, Tenant tenant);
    List<Membership> findAllBySubscriptionStatus(SubscriptionStatus status);



    Double countByEmployment_AndTenant(Employment employment, Tenant tenant);
    Double countBySubscriptionStatus_AndTenant(SubscriptionStatus status, Tenant tenant);

    Double countBySubscriptionPlan_AndTenant(SubscriptionPlan subscriptionPlan, Tenant tenant);
}
