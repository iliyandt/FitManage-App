package demos.springdata.fitmanage.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "visits")
@Entity
public class Visit extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    @Column(name = "gym_id", nullable = false)
    private Long userId;

    @Column(name = "check_in_at", nullable = false)
    private LocalDateTime checkInAt;

    @PrePersist
    public void onCreate() {
        this.checkInAt = LocalDateTime.now();
    }

    public Membership getMembership() {
        return membership;
    }

    public Visit setMembership(Membership membership) {
        this.membership = membership;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Visit setUserId(Long userId) {
        this.userId = userId;
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
