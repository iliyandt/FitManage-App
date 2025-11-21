package demos.springdata.fitmanage.domain.dto.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantLookUp {
    private Long tenantId;
    private String name;
    private String city;
    private String address;
}
