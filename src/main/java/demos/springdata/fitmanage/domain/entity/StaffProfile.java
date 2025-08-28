package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.StaffRole;
import jakarta.persistence.*;

@Entity
@Table(name = "staff_profiles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "tenant_id"})
        })
public class StaffProfile extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    private StaffRole staffProfile;


    public StaffProfile() {
    }

    public User getUser() {
        return user;
    }

    public StaffProfile setUser(User user) {
        this.user = user;
        return this;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public StaffProfile setTenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public StaffRole getStaffProfile() {
        return staffProfile;
    }

    public StaffProfile setStaffProfile(StaffRole staffProfile) {
        this.staffProfile = staffProfile;
        return this;
    }
}
