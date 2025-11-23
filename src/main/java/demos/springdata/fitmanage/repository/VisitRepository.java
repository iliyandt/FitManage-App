package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    Optional<List<Visit>> findByUser_Id(UUID userId);
    @Query("SELECT v FROM Visit v WHERE v.user.tenant = :tenant AND v.checkInAt BETWEEN :start AND :end")
    List<Visit> findByUserTenantAndCheckInAtBetween(Tenant tenant, LocalDateTime start, LocalDateTime end);
}
