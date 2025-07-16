package demos.springdata.fitmanage.domain.dto.common;

public class PaginationConfigDto {
    private int pageSize;

    public PaginationConfigDto() {
    }

    public PaginationConfigDto(int page, int pageSize, long totalRows) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


}
