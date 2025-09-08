package demos.springdata.fitmanage.domain.dto.shift;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class ShiftResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String role;
    private boolean approved;
    private String notes;

    public ShiftResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public ShiftResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public ShiftResponseDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public ShiftResponseDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public ShiftResponseDto setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public ShiftResponseDto setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getRole() {
        return role;
    }

    public ShiftResponseDto setRole(String role) {
        this.role = role;
        return this;
    }

    public boolean isApproved() {
        return approved;
    }

    public ShiftResponseDto setApproved(boolean approved) {
        this.approved = approved;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public ShiftResponseDto setNotes(String notes) {
        this.notes = notes;
        return this;
    }
}