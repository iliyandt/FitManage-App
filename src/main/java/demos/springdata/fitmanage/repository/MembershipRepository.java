package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Membership;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findAll(Specification<Membership> spec);
}
