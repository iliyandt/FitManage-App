package demos.springdata.fitmanage.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "visits")
@Entity
public class Visit extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "check_in_at", nullable = false)
    private LocalDateTime checkInAt;

    @PrePersist
    public void onCreate() {
        this.checkInAt = LocalDateTime.now();
    }

    public Visit() {
    }

    public Membership getMembership() {
        return membership;
    }

    public Visit setMembership(Membership membership) {
        this.membership = membership;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Visit setUser(User user) {
        this.user = user;
        return this;
    }

    public LocalDateTime getCheckInAt() {
        return checkInAt;
    }

    public Visit setCheckInAt(LocalDateTime checkInAt) {
        this.checkInAt = checkInAt;
        return this;
    }
}
