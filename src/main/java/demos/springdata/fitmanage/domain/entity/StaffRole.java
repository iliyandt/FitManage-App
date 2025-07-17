package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.StaffPosition;
import jakarta.persistence.*;

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
}
