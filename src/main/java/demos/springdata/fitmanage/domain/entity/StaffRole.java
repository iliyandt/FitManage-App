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

    public void setName(String name) {
        this.name = name;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public PredefinedStaffRole getPredefinedStaffRole() {
        return predefinedStaffRole;
    }

    public void setPredefinedStaffRole(PredefinedStaffRole predefinedStaffRole) {
        this.predefinedStaffRole = predefinedStaffRole;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
