package demos.springdata.fitmanage.domain.dto.gymmember;

import demos.springdata.fitmanage.domain.dto.ColumnConfigDto;
import demos.springdata.fitmanage.domain.dto.ConfigDto;

import java.util.List;
import java.util.Map;

public class GymMemberTableResponseDto {
    private ConfigDto config;
    private List<ColumnConfigDto> columns;
    private List<Map<String, Object>> rows;

    public GymMemberTableResponseDto() {
    }

    public GymMemberTableResponseDto(ConfigDto config, List<ColumnConfigDto> columns, List<Map<String, Object>> rows) {
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

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object >> rows) {
        this.rows = rows;
    }
}
