package demos.springdata.fitmanage.domain.dto.users;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTableDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UserTableDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String username;
    private Set<RoleType> roles = new HashSet<>();
    private OffsetDateTime birthDate;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
