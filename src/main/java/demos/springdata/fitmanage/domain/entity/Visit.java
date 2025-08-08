package demos.springdata.fitmanage.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "visits")
@Entity
public class Visit extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private GymMember gymMember;

    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @Column(name = "check_in_at", nullable = false)
    private LocalDateTime checkInAt;

    @PrePersist
    public void onCreate() {
        this.checkInAt = LocalDateTime.now();
    }

    public GymMember getGymMember() {
        return gymMember;
    }

    public Visit setGymMember(GymMember gymMember) {
        this.gymMember = gymMember;
        return this;
    }

    public Long getGymId() {
        return gymId;
    }

    public Visit setGymId(Long gymId) {
        this.gymId = gymId;
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
