package demos.springdata.fitmanage.domain.dto.common.config;

import java.util.List;
import java.util.Map;

public class ConfigDto {
    private SortingConfigDto sortable;
    private List<ActionConfigDto> actions;
    private ColumnsLayoutConfigDto columnsLayoutConfig;
    private Map<String, Boolean> createFields;
    private PaginationConfigDto pagination;

    public ConfigDto() {
    }

    public SortingConfigDto getSortable() {
        return sortable;
    }

    public ConfigDto setSortable(SortingConfigDto sortable) {
        this.sortable = sortable;
        return this;
    }

    public List<ActionConfigDto> getActions() {
        return actions;
    }

    public ConfigDto setActions(List<ActionConfigDto> actions) {
        this.actions = actions;
        return this;
    }

    public ColumnsLayoutConfigDto getColumnsLayoutConfig() {
        return columnsLayoutConfig;
    }

    public ConfigDto setColumnsLayoutConfig(ColumnsLayoutConfigDto columnsLayoutConfig) {
        this.columnsLayoutConfig = columnsLayoutConfig;
        return this;
    }

    public Map<String, Boolean> getCreateFields() {
        return createFields;
    }

    public ConfigDto setCreateFields(Map<String, Boolean> createFields) {
        this.createFields = createFields;
        return this;
    }

    public PaginationConfigDto getPagination() {
        return pagination;
    }

    public ConfigDto setPagination(PaginationConfigDto pagination) {
        this.pagination = pagination;
        return this;
    }
}
