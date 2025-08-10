package demos.springdata.fitmanage.domain.dto.visit;

import java.time.LocalDateTime;

public class VisitDto {
    private Long gymMemberId;
    private Long gymId;
    private LocalDateTime checkInAt;

    public VisitDto() {
    }

    public Long getGymMemberId() {
        return gymMemberId;
    }

    public VisitDto setGymMemberId(Long gymMemberId) {
        this.gymMemberId = gymMemberId;
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
