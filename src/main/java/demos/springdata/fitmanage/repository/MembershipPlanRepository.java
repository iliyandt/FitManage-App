package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

}
