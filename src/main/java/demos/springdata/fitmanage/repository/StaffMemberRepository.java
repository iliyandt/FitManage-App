package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<StaffMember> findAllByGym(Gym gym);
}
