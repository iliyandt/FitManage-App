package demos.springdata.fitmanage.domain.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "shifts")
public class Shift extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private boolean approved;
    private String notes;

    public Shift() {
    }

    public User getUser() {
        return user;
    }

    public Shift setUser(User user) {
        this.user = user;
        return this;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public Shift setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public Shift setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public boolean isApproved() {
        return approved;
    }

    public Shift setApproved(boolean approved) {
        this.approved = approved;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public Shift setNotes(String notes) {
        this.notes = notes;
        return this;
    }
}
