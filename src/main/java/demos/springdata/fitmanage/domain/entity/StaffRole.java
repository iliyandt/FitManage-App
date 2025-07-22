package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.Permission;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "staff_roles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "gym_id"}))
public class StaffRole extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predefined_role_id")
    private PredefinedStaffRole predefinedStaffRole;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "staff_role_permissions",
            joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    private Set<Permission> permissions = new HashSet<>();

    public StaffRole() {
    }

    public String getName() {
        return name;
    }

    public StaffRole setName(String name) {
        this.name = name;
        return this;
    }

    public Gym getGym() {
        return gym;
    }

    public StaffRole setGym(Gym gym) {
        this.gym = gym;
        return this;
    }

    public PredefinedStaffRole getPredefinedStaffRole() {
        return predefinedStaffRole;
    }

    public StaffRole setPredefinedStaffRole(PredefinedStaffRole predefinedStaffRole) {
        this.predefinedStaffRole = predefinedStaffRole;
        return this;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public StaffRole setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }
}
