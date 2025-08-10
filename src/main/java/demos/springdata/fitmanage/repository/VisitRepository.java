package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByGymMember_Id(Long memberId);
    List<Visit> findByGymMemberIdAndCheckInAtBetween(Long id, LocalDateTime checkInAtAfter, LocalDateTime checkInAtBefore);
}
