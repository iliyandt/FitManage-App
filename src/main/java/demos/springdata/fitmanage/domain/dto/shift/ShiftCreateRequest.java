package demos.springdata.fitmanage.domain.dto.shift;

import java.time.LocalDateTime;

public class ShiftCreateRequest {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;

    public ShiftCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public ShiftCreateRequest setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public ShiftCreateRequest setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ShiftCreateRequest setEndTime(LocalDateTime endTime) {
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
