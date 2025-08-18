package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.converter.MapToJsonConverter;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "gym_account_settings")
public class GymAccountSettings extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "gym_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> settings = new HashMap<>();

    public GymAccountSettings() {
    }

    public User getUser() {
        return user;
    }

    public GymAccountSettings setUser(User user) {
        this.user = user;
        return this;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public GymAccountSettings setSettings(Map<String, Object> settings) {
        this.settings = settings;
        return this;
    }
}
