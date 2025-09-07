package demos.springdata.fitmanage.domain.dto.shift;

import java.time.LocalDateTime;

public class ShiftDto {
    private Long id;
    private String employeeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String role;
    private boolean approved;

    public ShiftDto() {
    }

    public Long getId() {
        return id;
    }

    public ShiftDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public ShiftDto setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public ShiftDto setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ShiftDto setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getRole() {
        return role;
    }

    public ShiftDto setRole(String role) {
        this.role = role;
        return this;
    }

    public boolean isApproved() {
        return approved;
    }

    public ShiftDto setApproved(boolean approved) {
        this.approved = approved;
        return this;
    }
}
