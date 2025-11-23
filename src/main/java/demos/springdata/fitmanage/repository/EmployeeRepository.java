package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Employee;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findAllByTenant_Id(UUID id);
    Employee findByTenant_IdAndUser_Id(UUID tenantId, UUID userId);
    Optional<Employee> findByIdAndTenant(UUID id, Tenant tenant);
    Employee findByUser(User user);
}
