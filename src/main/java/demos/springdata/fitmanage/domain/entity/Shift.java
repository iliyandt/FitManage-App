package demos.springdata.fitmanage.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
public class Shift extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean approved;
    private String notes;

    public Shift() {
    }

    public Employee getEmployee() {
        return employee;
    }

    public Shift setEmployee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public Shift setTenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Shift setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Shift setEndTime(LocalDateTime endTime) {
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
