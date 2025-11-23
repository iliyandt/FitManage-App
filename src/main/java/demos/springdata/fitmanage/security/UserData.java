package demos.springdata.fitmanage.security;

import demos.springdata.fitmanage.domain.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserData implements UserDetails {
    private UUID id;
    private String email;
    private String password;
    private Set<Role> roles;
    private boolean enabled;

    public UserData(UUID id, String email, String password, Set<Role> roles, boolean enabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.enabled = enabled;
    }

    public UUID getId() {
        return id;
    }

    public UserData setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserData setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserData setPassword(String password) {
        this.password = password;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public UserData setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public UserData setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return this.enabled; }
    @Override
    public boolean isAccountNonLocked() { return this.enabled; }
    @Override
    public boolean isCredentialsNonExpired() { return this.enabled; }
    @Override
    public boolean isEnabled() { return this.enabled; }
}
