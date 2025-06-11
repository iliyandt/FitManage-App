package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
