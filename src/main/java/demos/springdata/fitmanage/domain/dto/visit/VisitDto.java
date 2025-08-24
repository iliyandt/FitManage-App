package demos.springdata.fitmanage.domain.dto.visit;


import java.time.LocalDateTime;

public class VisitDto {
    private Long userId;
    private Long membershipId;
    private LocalDateTime checkInAt;

    public VisitDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public VisitDto setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public VisitDto setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
        return this;
    }

    public LocalDateTime getCheckInAt() {
        return checkInAt;
    }

    public VisitDto setCheckInAt(LocalDateTime checkInAt) {
        this.checkInAt = checkInAt;
        return this;
    }
}
