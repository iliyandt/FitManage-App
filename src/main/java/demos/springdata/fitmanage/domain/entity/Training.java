package demos.springdata.fitmanage.domain.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainings")
public class Training extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Column(nullable = false)
    private String title;

    private String category;

    private String location;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToMany
    @JoinTable(
            name = "training_participants",
            joinColumns = @JoinColumn(name = "training_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    public Training() {
    }

    public Tenant getTenant() {
        return tenant;
    }

    public Training setTenant(Tenant tenant) {
        this.tenant = tenant;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Training setTitle(String name) {
        this.title = name;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Training setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Training setLocation(String location) {
        this.location = location;
        return this;
    }

    public Instant getDate() {
        return date;
    }

    public Training setDate(Instant startTime) {
        this.date = startTime;
        return this;
    }

    public Integer getDuration() {
        return duration;
    }

    public Training setDuration(Integer durationInMinutes) {
        this.duration = durationInMinutes;
        return this;
    }

    public User getTrainer() {
        return trainer;
    }

    public Training setTrainer(User trainer) {
        this.trainer = trainer;
        return this;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Training setCapacity(Integer capacity) {
        this.capacity = capacity;
        return this;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public Training setParticipants(Set<User> participants) {
        this.participants = participants;
        return this;
    }
}
