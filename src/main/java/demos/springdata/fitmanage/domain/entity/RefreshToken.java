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


    public RefreshToken() {
    }

    public RefreshToken(String token, Instant expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
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
}