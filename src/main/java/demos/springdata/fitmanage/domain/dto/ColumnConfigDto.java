package demos.springdata.fitmanage.domain.dto;

public class ColumnConfigDto {
    private String field;
    private String header;

    public ColumnConfigDto() {
    }

    public ColumnConfigDto(String field, String header) {
        this.field = field;
        this.header = header;
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
}
