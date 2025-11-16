package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Employee;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByTenant_Id(Long id);
    Employee findByTenant_IdAndUser_Id(Long id, Long id1);
    Optional<Employee> findByIdAndTenant(Long id, Tenant tenant);

    Employee findByUser(User user);
}
