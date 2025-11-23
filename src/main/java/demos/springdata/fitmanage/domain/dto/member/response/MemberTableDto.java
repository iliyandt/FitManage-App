package demos.springdata.fitmanage.domain.dto.member.response;

import demos.springdata.fitmanage.annotation.DropDown;
import demos.springdata.fitmanage.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTableDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Set<RoleType> roles = new HashSet<>();
    private OffsetDateTime birthDate;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Employment employment;
    private SubscriptionStatus subscriptionStatus;
    @DropDown(url = "/memberships")
    private SubscriptionPlan subscriptionPlan;
    private Integer allowedVisits;
    private Integer remainingVisits;

}