package demos.springdata.fitmanage.domain.dto;

import java.util.List;

public class ConfigDto {
    private boolean sortable;
    private List<ActionConfigDto> actions;
    private PaginationConfigDto pagination;

    public ConfigDto() {
    }

    public ConfigDto(boolean sortable, List<ActionConfigDto> actions, PaginationConfigDto pagination) {
        this.sortable = sortable;
        this.actions = actions;
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

    public PaginationConfigDto getPagination() {
        return pagination;
    }

    public void setPagination(PaginationConfigDto pagination) {
        this.pagination = pagination;
    }
}
