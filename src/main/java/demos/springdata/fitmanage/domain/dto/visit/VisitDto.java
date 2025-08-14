package demos.springdata.fitmanage.domain.dto.visit;

import java.time.LocalDateTime;

public class VisitDto {
    private Long memberId;
    private Long gymId;
    private LocalDateTime checkInAt;

    public VisitDto() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public VisitDto setMemberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public Long getGymId() {
        return gymId;
    }

    public VisitDto setGymId(Long gymId) {
        this.gymId = gymId;
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
