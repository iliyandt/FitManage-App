package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.News;
import demos.springdata.fitmanage.domain.enums.NewsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query(
            "SELECT DISTINCT n FROM News n " +
                    "WHERE n.status = :status " +
                    "AND n.tenantId = :tenantId " +
                    "AND (" +
                    "    :isAdmin = TRUE OR " +
                    "    (" +
                    "        n.publicationType = 'ALL' OR " +
                    "        (" +
                    "            n.publicationType = 'TARGETED' AND " +
                    "            EXISTS (SELECT r FROM n.recipients r WHERE r.id = :userId)" +
                    "        )" +
                    "    )" +
                    ")"
    )
    List<News> findAllPublishedForUser(@Param("userId") Long userId,
                                       @Param("status") NewsStatus status,
                                       @Param("tenantId") Long tenantId,
                                       @Param("isAdmin") boolean isAdmin);

    News getNewsById(Long id);
}
