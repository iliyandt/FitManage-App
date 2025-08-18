package demos.springdata.fitmanage.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Builder
public class RefreshToken extends BaseEntity {
    private String token;
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne
    @JoinColumn(name = "super_admin_id", referencedColumnName = "id")
    private SuperAdminUser superAdminUser;

    public RefreshToken() {
    }

    public RefreshToken(String token, Instant expiryDate, User user, SuperAdminUser superAdminUser) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
        this.superAdminUser = superAdminUser;
    }

    public String getToken() {
        return token;
    }

    public RefreshToken setToken(String token) {
        this.token = token;
        return this;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public RefreshToken setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public User getUser() {
        return user;
    }

    public RefreshToken setUser(User user) {
        this.user = user;
        return this;
    }

    public SuperAdminUser getSuperAdminUser() {
        return superAdminUser;
    }

    public RefreshToken setSuperAdminUser(SuperAdminUser superAdminUser) {
        this.superAdminUser = superAdminUser;
        return this;
    }
}