package demos.springdata.fitmanage.domain.dto;

public class ConfigDto {
    private String title;
    private boolean sortable;
    private PaginationConfigDto pagination;

    public ConfigDto() {
    }

    public ConfigDto(String title, boolean sortable, PaginationConfigDto pagination) {
        this.title = title;
        this.sortable = sortable;
        this.pagination = pagination;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public PaginationConfigDto getPagination() {
        return pagination;
    }

    public void setPagination(PaginationConfigDto pagination) {
        this.pagination = pagination;
    }
}
