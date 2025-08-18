package demos.springdata.fitmanage.domain.entity;

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

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_role_id", nullable = false)
    private StaffRole staffRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;


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

    public String getFirstName() {
        return firstName;
    }

    public StaffProfile setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public StaffProfile setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public StaffRole getStaffRole() {
        return staffRole;
    }

    public StaffProfile setStaffRole(StaffRole staffRole) {
        this.staffRole = staffRole;
        return this;
    }

    public Gym getGym() {
        return gym;
    }

    public StaffProfile setGym(Gym gym) {
        this.gym = gym;
        return this;
    }
}
