package demos.springdata.fitmanage.domain.dto.common.config;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigDto {
    private SortingConfigDto sortable;
    private List<ActionConfig> actions;
    private ColumnsLayoutConfig columnsLayoutConfig;
    private Map<String, Boolean> createFields;
    private PaginationConfig pagination;

    public ConfigDto setSortable(SortingConfigDto sortable) {
        this.sortable = sortable;
        return this;
    }

    public ConfigDto setActions(List<ActionConfig> actions) {
        this.actions = actions;
        return this;
    }

    public ConfigDto setColumnsLayoutConfig(ColumnsLayoutConfig columnsLayoutConfig) {
        this.columnsLayoutConfig = columnsLayoutConfig;
        return this;
    }

    public ConfigDto setCreateFields(Map<String, Boolean> createFields) {
        this.createFields = createFields;
        return this;
    }

    public ConfigDto setPagination(PaginationConfig pagination) {
        this.pagination = pagination;
        return this;
    }
}
