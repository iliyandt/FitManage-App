package demos.springdata.fitmanage.domain.entity;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "staff_members")
public class StaffMember extends BaseEntity implements UserDetails {
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_role_id", nullable = false)
    private StaffRole staffRole;


    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "staff_member_security_roles",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    private boolean enabled = true;

    public StaffMember() {
    }

    public String getFirstName() {
        return firstName;
    }

    public StaffMember setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public StaffMember setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public StaffMember setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public StaffMember setPassword(String password) {
        this.password = password;
        return this;
    }

    public StaffRole getStaffRole() {
        return staffRole;
    }

    public StaffMember setStaffRole(StaffRole staffRole) {
        this.staffRole = staffRole;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public StaffMember setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public StaffMember setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Gym getGym() {
        return gym;
    }

    public StaffMember setGym(Gym gym) {
        this.gym = gym;
        return this;
    }

    public StaffMember setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public StaffMember setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
