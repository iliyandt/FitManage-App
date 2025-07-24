package demos.springdata.fitmanage.domain.dto.common.config;

import java.util.List;
import java.util.Map;

public class ConfigDto {
    private boolean sortable;
    private List<ActionConfigDto> actions;
    private ColumnsLayoutConfigDto columnsLayoutConfig;
    private Map<String, Boolean> createFields;
    private PaginationConfigDto pagination;

    public ConfigDto() {
    }

    public ConfigDto(boolean sortable, List<ActionConfigDto> actions, ColumnsLayoutConfigDto columnsLayoutConfig, Map<String, Boolean> createFields, PaginationConfigDto pagination) {
        this.sortable = sortable;
        this.actions = actions;
        this.columnsLayoutConfig = columnsLayoutConfig;
        this.createFields = createFields;
        this.pagination = pagination;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public List<ActionConfigDto> getActions() {
        return actions;
    }

    public void setActions(List<ActionConfigDto> actions) {
        this.actions = actions;
    }

    public ColumnsLayoutConfigDto getColumnsLayoutConfig() {
        return columnsLayoutConfig;
    }

    public void setColumnsLayoutConfig(ColumnsLayoutConfigDto columnsLayoutConfig) {
        this.columnsLayoutConfig = columnsLayoutConfig;
    }

    public Map<String, Boolean> getCreateFields() {
        return createFields;
    }

    public void setCreateFields(Map<String, Boolean> createFields) {
        this.createFields = createFields;
    }

    public PaginationConfigDto getPagination() {
        return pagination;
    }

    public void setPagination(PaginationConfigDto pagination) {
        this.pagination = pagination;
    }
}
