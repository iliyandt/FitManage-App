package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
}
