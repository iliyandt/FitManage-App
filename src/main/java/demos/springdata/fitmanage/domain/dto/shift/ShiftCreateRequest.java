package demos.springdata.fitmanage.domain.dto.shift;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class ShiftCreateRequest {
    private Long id;
    private String name;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String notes;

    public ShiftCreateRequest() {
    }

    public Long getId() {
        return id;
    }

    public ShiftCreateRequest setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ShiftCreateRequest setName(String name) {
        this.name = name;
        return this;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public ShiftCreateRequest setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public ShiftCreateRequest setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public ShiftCreateRequest setNotes(String notes) {
        this.notes = notes;
        return this;
    }
}
