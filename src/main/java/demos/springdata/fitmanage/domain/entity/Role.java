package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.RoleType;
import jakarta.persistence.*;


@Entity
@Table(name = "security_roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    public Role() {
    }

    public Role(RoleType name) {
        this.name = name;
    }

    public RoleType getName() {
        return name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }

}
