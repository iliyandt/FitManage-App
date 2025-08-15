package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByGymMember_Id(Long memberId);
    @Query("SELECT v FROM Visit v WHERE v.gymId = :gymId AND v.checkInAt BETWEEN :start AND :end")
    List<Visit> findVisitsBetweenDates(@Param("gymId") Long gymId,
                               @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);
}
