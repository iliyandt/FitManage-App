package demos.springdata.fitmanage.domain.dto.users;

import demos.springdata.fitmanage.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Gender gender;
    private Set<RoleType> roles = new HashSet<>();
    private OffsetDateTime birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String phone;
    private String address;
    private String city;
}
