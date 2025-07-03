package demos.springdata.fitmanage.domain.dto.gym;

import demos.springdata.fitmanage.domain.dto.ColumnConfigDto;
import demos.springdata.fitmanage.domain.dto.ConfigDto;

import java.util.List;
import java.util.Map;

public class GymMemberTableResponseDto {
    private ConfigDto config;
    private List<ColumnConfigDto> columns;
    private List<Map<String, String>> rows;

    public GymMemberTableResponseDto() {
    }

    public GymMemberTableResponseDto(ConfigDto config, List<ColumnConfigDto> columns, List<Map<String, String>> rows) {
        this.config = config;
        this.columns = columns;
        this.rows = rows;
    }

    public ConfigDto getConfig() {
        return config;
    }

    public void setConfig(ConfigDto config) {
        this.config = config;
    }

    public List<ColumnConfigDto> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnConfigDto> columns) {
        this.columns = columns;
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, String >> rows) {
        this.rows = rows;
    }
}
