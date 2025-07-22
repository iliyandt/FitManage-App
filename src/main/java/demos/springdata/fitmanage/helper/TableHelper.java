package demos.springdata.fitmanage.helper;

import demos.springdata.fitmanage.domain.dto.common.ActionConfigDto;
import demos.springdata.fitmanage.domain.dto.common.ColumnsLayoutConfigDto;
import demos.springdata.fitmanage.domain.dto.common.ConfigDto;
import demos.springdata.fitmanage.domain.dto.common.PaginationConfigDto;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TableHelper {
    public <T> List<Map<String, Object>> buildRows(List<T> data, RowMapper<T> rowMapper) {
        return data.stream()
                .map(rowMapper::mapRow)
                .toList();
    }

    public <T> ConfigDto buildTableConfig(String basePath, Class<T> dtoClass) {
        PaginationConfigDto pagination = new PaginationConfigDto();
        pagination.setPageSize(10);

        List<ActionConfigDto> actions = List.of(
                new ActionConfigDto("details", "Details", basePath + "/{id}"),
                new ActionConfigDto("edit", "Edit", basePath + "/{id}"),
                new ActionConfigDto("delete", "Delete", basePath + "/{id}")
        );

        Map<String, Boolean> columnVisibility = buildColumnVisibility(dtoClass, true);
        ColumnsLayoutConfigDto columnsLayoutConfig = new ColumnsLayoutConfigDto(columnVisibility);

        ConfigDto config = new ConfigDto();
        config.setSortable(true);
        config.setActions(actions);
        config.setPagination(pagination);
        config.setColumnsLayoutConfig(columnsLayoutConfig);

        return config;
    }

    public <T> Map<String, Object> buildRowMap(T dto) {
        Map<String, Object> row = new LinkedHashMap<>();
        Field[] fields = dto.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                row.put(field.getName(), value != null ? value : "N/A");
            } catch (IllegalAccessException e) {
                row.put(field.getName(), "ERROR");
            }
        }

        return row;
    }

    public <T> Map<String, Boolean> buildColumnVisibility(Class<T> dtoClass, boolean defaultVisible) {
        Map<String, Boolean> columnVisibility = new LinkedHashMap<>();
        Field[] fields = dtoClass.getDeclaredFields();

        for (Field field : fields) {
            String name = field.getName();
            columnVisibility.put(name, defaultVisible);
            //todo: make the visibility custom for every table
        }

        return columnVisibility;
    }

}
