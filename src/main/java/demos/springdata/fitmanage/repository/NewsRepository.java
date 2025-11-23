package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.News;
import demos.springdata.fitmanage.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {
    News getNewsById(UUID id);
    List<News> findAllByTenantId(UUID tenantId);

}
