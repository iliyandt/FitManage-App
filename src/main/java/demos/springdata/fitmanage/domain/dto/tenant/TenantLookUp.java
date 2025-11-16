package demos.springdata.fitmanage.domain.dto.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantLookUp {
    private Long tenantId;
    private String name;
    private String city;
    private String address;
}
