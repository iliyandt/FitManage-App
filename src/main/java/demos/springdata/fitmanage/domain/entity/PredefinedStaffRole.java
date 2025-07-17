package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.Permission;
import demos.springdata.fitmanage.domain.enums.StaffPosition;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "predefined_staff_roles")
public class PredefinedStaffRole extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private StaffPosition position;

    @Column(length = 500)
    private String description;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "predefined_role_permissions",
            joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    private Set<Permission> defaultPermissions = new HashSet<>();


    public PredefinedStaffRole() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StaffPosition getPosition() {
        return position;
    }

    public void setPosition(StaffPosition position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permission> getDefaultPermissions() {
        return defaultPermissions;
    }

    public void setDefaultPermissions(Set<Permission> defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }
}
