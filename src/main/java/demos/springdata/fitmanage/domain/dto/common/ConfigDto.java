package demos.springdata.fitmanage.domain.dto.common;

import java.util.List;

public class ConfigDto {
    private boolean sortable;
    private List<ActionConfigDto> actions;
    private ColumnsLayoutConfigDto columnsLayoutConfig;
    private PaginationConfigDto pagination;

    public ConfigDto() {
    }

    public ConfigDto(boolean sortable, List<ActionConfigDto> actions, ColumnsLayoutConfigDto columnsLayoutConfig, PaginationConfigDto pagination) {
        this.sortable = sortable;
        this.actions = actions;
        this.columnsLayoutConfig = columnsLayoutConfig;
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

    public PaginationConfigDto getPagination() {
        return pagination;
    }

    public void setPagination(PaginationConfigDto pagination) {
        this.pagination = pagination;
    }
}
