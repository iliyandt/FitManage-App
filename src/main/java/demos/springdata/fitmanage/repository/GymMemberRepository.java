package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.GymMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, Long> {
}
