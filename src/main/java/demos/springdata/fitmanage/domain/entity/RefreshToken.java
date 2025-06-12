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

    public RefreshToken() {
    }

    public RefreshToken(String token, Instant expiryDate, Gym gym) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.gym = gym;
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
}
