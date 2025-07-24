package demos.springdata.fitmanage.domain.dto.common.config;

public class ColumnConfigDto {
    private String field;
    private String header;
    private String type;
    private DropDownConfig dropDownConfig;

    public ColumnConfigDto() {
    }

    public ColumnConfigDto(String field, String header, String type) {
        this.field = field;
        this.header = header;
        this.type = type;
    }

    public ColumnConfigDto(String field, String header, String type, DropDownConfig dropDownConfig) {
        this.field = field;
        this.header = header;
        this.type = type;
        this.dropDownConfig = dropDownConfig;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DropDownConfig getDropDownConfig() {
        return dropDownConfig;
    }

    public void setDropDownConfig(DropDownConfig dropDownConfig) {
        this.dropDownConfig = dropDownConfig;
    }
}
