package demos.springdata.fitmanage.helper;

import demos.springdata.fitmanage.domain.dto.ActionConfigDto;
import demos.springdata.fitmanage.domain.dto.ConfigDto;
import demos.springdata.fitmanage.domain.dto.PaginationConfigDto;
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

    public ConfigDto buildTableConfig(String basePath) {
        PaginationConfigDto pagination = new PaginationConfigDto();
        pagination.setPageSize(10);

        List<ActionConfigDto> actions = List.of(
                new ActionConfigDto("details", "Details", basePath + "/{id}"),
                new ActionConfigDto("edit", "Edit", basePath + "/{id}"),
                new ActionConfigDto("delete", "Delete", basePath + "/{id}")
        );

        ConfigDto config = new ConfigDto();
        config.setSortable(true);
        config.setActions(actions);
        config.setPagination(pagination);

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
}
