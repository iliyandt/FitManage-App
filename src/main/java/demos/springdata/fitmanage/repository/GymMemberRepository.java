package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, Long>, JpaSpecificationExecutor<GymMember> {
    Optional<GymMember> findByEmail(String email);
    boolean existsByEmailAndGymEmail(String email, String gymEmail);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndGymEmail(String phone, String gymEmail);
    List<GymMember> findGymMembersByGym(Gym gym);
    List<GymMember> findBySubscriptionStatus(SubscriptionStatus subscriptionStatus);
}
