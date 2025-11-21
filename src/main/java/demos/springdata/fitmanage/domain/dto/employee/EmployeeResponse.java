package demos.springdata.fitmanage.domain.dto.employee;

import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeResponse {
//    private Long id;
//    private String firstName;
//    private String lastName;
//    private String username;
//    private String email;
//    private Gender gender;
//    private Set<RoleType> roles = new HashSet<>();
//    private OffsetDateTime birthDate;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private String phone;
//    private String address;
//    private String city;
    private EmployeeRole employeeRole;
}
