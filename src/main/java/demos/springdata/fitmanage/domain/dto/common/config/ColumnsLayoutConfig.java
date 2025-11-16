package demos.springdata.fitmanage.domain.dto.common.config;

import java.util.Map;

public class ColumnsLayoutConfig {
    private Map<String, Boolean> columnVisibility;


    public ColumnsLayoutConfig(Map<String, Boolean> columnVisibility) {
        this.columnVisibility = columnVisibility;
    }

    public Map<String, Boolean> getColumnVisibility() {
        return columnVisibility;
    }

    public void setColumnVisibility(Map<String, Boolean> columnVisibility) {
        this.columnVisibility = columnVisibility;
    }
}
