package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, Long>, JpaSpecificationExecutor<GymMember> {


    @Query("SELECT gm FROM GymMember gm JOIN FETCH gm.gym WHERE gm.email = :email")
    Optional<GymMember> findByEmailWithGym(@Param("email") String email);

    boolean existsByEmailAndGymEmail(String email, String gymEmail);
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndGymEmail(String phone, String gymEmail);
    List<GymMember> findGymMembersByGym(Gym gym);
    List<GymMember> findBySubscriptionStatus(SubscriptionStatus subscriptionStatus);
    Optional<GymMember> findByEmail(String email);

    //Search by
    Optional<GymMember> findByIdAndGym_Id(Long id, Long gymId);
    Optional<GymMember> findByEmailIgnoreCaseAndGym_Id(String email, Long gymId);
    Optional<GymMember> findByPhoneIgnoreCaseAndGym_Id(String phone, Long gymId);
    Optional<GymMember> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndGym_Id(String firstName, String lastName, Long gymId);
}
