package demos.springdata.fitmanage.domain.dto.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationConfig {
    private int pageSize;
}
