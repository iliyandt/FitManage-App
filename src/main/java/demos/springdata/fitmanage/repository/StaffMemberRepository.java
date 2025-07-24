package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT s FROM StaffMember s JOIN FETCH s.staffRole WHERE s.gym = :gym")
    List<StaffMember> findAllByGymWithRole(@Param("gym") Gym gym);
}
