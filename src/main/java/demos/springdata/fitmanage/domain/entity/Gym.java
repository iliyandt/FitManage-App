package demos.springdata.fitmanage.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "gyms")
public class Gym extends BaseEntity {

    @Column(name = "subscription_valid_until")
    private LocalDate subscriptionValidUntil;

    @OneToMany(mappedBy = "tenant")
    private List<User> users = new ArrayList<>();


    public Gym() {
    }


}
