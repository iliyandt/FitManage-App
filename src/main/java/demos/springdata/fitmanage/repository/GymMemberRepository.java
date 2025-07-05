package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.GymMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, Long> {
    Optional<GymMember> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
