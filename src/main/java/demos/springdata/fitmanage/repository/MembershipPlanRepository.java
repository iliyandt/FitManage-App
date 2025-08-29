package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> getMembershipPlansByUser_Id(Long id);
    MembershipPlan getMembershipPlanById(Long id);
}
