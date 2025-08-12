package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.converter.JsonMapConverter;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "gym_account_settings")
public class GymAccountSettings extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "gym_id", nullable = false, unique = true)
    private Gym gym;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings = new HashMap<>();

    public GymAccountSettings() {
    }

    public Gym getGym() {
        return gym;
    }

    public GymAccountSettings setGym(Gym gym) {
        this.gym = gym;
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
