package demos.springdata.fitmanage.domain.dto.common.config;

public class SortingConfigDto {
    private String field;
    private boolean desc;

    public SortingConfigDto() {
    }

    public String getField() {
        return field;
    }

    public SortingConfigDto setField(String field) {
        this.field = field;
        return this;
    }

    public boolean isDesc() {
        return desc;
    }

    public SortingConfigDto setDesc(Boolean desc) {
        this.desc = desc;
        return this;
    }
}
