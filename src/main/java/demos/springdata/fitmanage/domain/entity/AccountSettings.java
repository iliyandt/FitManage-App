package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.converter.MapToJsonConverter;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "account_settings")
public class AccountSettings extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> settings = new HashMap<>();

    public AccountSettings() {
    }

    public User getUser() {
        return user;
    }

    public AccountSettings setUser(User user) {
        this.user = user;
        return this;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public AccountSettings setSettings(Map<String, Object> settings) {
        this.settings = settings;
        return this;
    }
}
