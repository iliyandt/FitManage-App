package demos.springdata.fitmanage.domain.dto;

public class ColumnConfigDto {
    private String field;
    private String header;
    private String type;

    public ColumnConfigDto() {
    }

    public ColumnConfigDto(String field, String header, String type) {
        this.field = field;
        this.header = header;
        this.type = type;
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
}
