package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import jakarta.persistence.*;

@Entity
@Table(name = "employees",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "tenant_id"})
        })
public class Employee extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    private EmployeeRole employeeRole;


    public Employee() {
    }

    public User getUser() {
        return user;
    }

    public Employee setUser(User user) {
        this.user = user;
        return this;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public Employee setTenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    public Employee setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
        return this;
    }
}
