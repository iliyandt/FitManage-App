package demos.springdata.fitmanage.domain.dto.member.response;

import demos.springdata.fitmanage.annotation.DropDown;
import demos.springdata.fitmanage.domain.dto.users.UserTableDto;
import demos.springdata.fitmanage.domain.enums.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MemberTableDto extends UserTableDto {
    private Employment employment;
    private SubscriptionStatus subscriptionStatus;
    @DropDown(url = "/memberships")
    private SubscriptionPlan subscriptionPlan;
    private Integer allowedVisits;
    private Integer remainingVisits;

}