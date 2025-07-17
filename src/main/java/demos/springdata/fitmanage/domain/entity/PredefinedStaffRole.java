package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.StaffPosition;
import jakarta.persistence.*;

@Entity
@Table(name = "predefined_staff_roles")
public class PredefinedStaffRole extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private StaffPosition position;

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
}
