package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.News;
import demos.springdata.fitmanage.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    News getNewsById(Long id);

    @Query("SELECT DISTINCT n FROM News n " +
            "LEFT JOIN n.targetRoles tr " +
            "WHERE n.tenantId = :tenantId " +
            "AND (n.author.id = :userId) OR (n.author IS NULL AND (n.targetRoles IS EMPTY OR tr.name IN :roles))"
    )
    List<News> findAllOrTargetedToUser(@Param("tenantId") Long tenantId,
                                       @Param("roles") Set<RoleType> roles,
                                       @Param("userId") Long userId
    );
}
