package demos.springdata.fitmanage.repository;

import demos.springdata.fitmanage.domain.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByTenant_Id(Long id);
    Employee findByTenant_IdAndUser_Id(Long id, Long id1);
}
