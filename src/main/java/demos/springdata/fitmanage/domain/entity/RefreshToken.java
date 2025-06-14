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
public class RefreshToken extends BaseEntity{
    private String token;
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "gym_id", referencedColumnName = "id")
    private Gym gym;

    @OneToOne
    @JoinColumn(name = "super_admin_id", referencedColumnName = "id")
    private SuperAdminUser superAdminUser;

    public RefreshToken() {
    }

    public RefreshToken(String token, Instant expiryDate, Gym gym, SuperAdminUser superAdminUser) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.gym = gym;
        this.superAdminUser = superAdminUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public SuperAdminUser getSuperAdminUser() {
        return superAdminUser;
    }

    public void setSuperAdminUser(SuperAdminUser superAdminUser) {
        this.superAdminUser = superAdminUser;
    }
}
