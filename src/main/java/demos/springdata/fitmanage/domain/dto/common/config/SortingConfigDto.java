package demos.springdata.fitmanage.domain.dto.common.config;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortingConfigDto {
    private String field;
    private boolean desc;

    public SortingConfigDto setField(String field) {
        this.field = field;
        return this;
    }

    public SortingConfigDto setDesc(boolean desc) {
        this.desc = desc;
        return this;
    }
}
