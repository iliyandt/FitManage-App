package demos.springdata.fitmanage.domain.dto.common.response;

import demos.springdata.fitmanage.domain.dto.common.config.ColumnConfig;
import demos.springdata.fitmanage.domain.dto.common.config.ConfigDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableResponseDto {
    private ConfigDto config;
    private List<ColumnConfig> columns;
    private List<Map<String, Object>> rows;
}
