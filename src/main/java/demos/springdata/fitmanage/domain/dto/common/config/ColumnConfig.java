package demos.springdata.fitmanage.domain.dto.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnConfig {
    private String field;
    private String header;
    private String type;
    private DropDownConfig dropDownConfig;
}
