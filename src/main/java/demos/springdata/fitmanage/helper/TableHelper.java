package demos.springdata.fitmanage.helper;

import demos.springdata.fitmanage.domain.dto.common.config.*;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTableDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.membershipplan.MembershipPlanTableDto;
import demos.springdata.fitmanage.domain.dto.training.TrainingRequest;
import demos.springdata.fitmanage.domain.dto.training.TrainingResponse;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TableHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(TableHelper.class);

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
        Map<String, Boolean> createFields = buildCreateFields(dtoClass);

        SortingConfigDto sortingConfig = sortingConfigMap.getOrDefault(
                dtoClass,
                new SortingConfigDto().setField("id").setDesc(true)
        );

        return new ConfigDto()
                .setSortable(sortingConfig)
                .setActions(actions)
                .setPagination(pagination)
                .setColumnsLayoutConfig(columnsLayoutConfig)
                .setCreateFields(createFields);
    }

    public <T> Map<String, Object> buildRowMap(T dto) {
        Map<String, Object> row = new LinkedHashMap<>();
        Field[] fields = dto.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(dto);
                row.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                row.put(field.getName(), "ERROR");
            }
        }

        return row;
    }

    public <T> Map<String, Boolean> buildColumnVisibility(Class<T> dtoClass, boolean defaultVisible) {
        Map<String, Boolean> columnVisibility = new LinkedHashMap<>();
        Set<String> visibleColumns = customColumnVisibilityMap.get(dtoClass);

        if (visibleColumns == null) {
            return columnVisibility;
        }

        Field[] fields = dtoClass.getDeclaredFields();

        for (Field field : fields) {
            String name = field.getName();
            boolean isContained = visibleColumns.contains(name);
            columnVisibility.put(name, isContained);
        }

        return columnVisibility;
    }

    private <T> Map<String, Boolean> buildCreateFields(Class<T> dtoClass) {
        Map<String, Boolean> createFields = new LinkedHashMap<>();

        LOGGER.info("Looking up createFields for DTO class: {}", dtoClass.getName());
        Set<String> allowedFields = customCreateFieldMap.get(dtoClass);

        if (allowedFields == null) {
            LOGGER.warn("No createFields config found for class: {}", dtoClass.getName());
            return createFields;
        }

        Field[] fields = dtoClass.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            boolean isAllowed = allowedFields.contains(fieldName);
            createFields.put(fieldName, isAllowed);
        }

        return createFields;
    }


    private static final Map<Class<?>, Set<String>> customCreateFieldMap = Map.of(
            MemberTableDto.class, Set.of(
                    "firstName",
                    "lastName",
                    "phone",
                    "email",
                    "gender",
                    "birthDate"
            ),


            MembershipPlanTableDto.class, Set.of(
                    "price",
                    "studentPrice",
                    "seniorPrice",
                    "handicapPrice"
            ),

            EmployeeTableDto.class, Set.of(
                    "firstName",
                    "lastName",
                    "email",
                    "username",
                    "gender",
                    "birthDate",
                    "phone",
                    "employeeRole"
            ),

            TrainingResponse.class, Set.of(
                    "title",
                    "category",
                    "date",
                    "duration",
                    "capacity",
                    "trainerId"
            )
    );

    private static final Map<Class<?>, Set<String>> customColumnVisibilityMap = Map.of(
            MemberTableDto.class, Set.of("id", "firstName", "lastName", "phone", "subscriptionStatus"),
            EmployeeTableDto.class, Set.of("id", "firstName", "lastName", "gender", "employeeRole"),
            MembershipPlanTableDto.class, Set.of("id", "subscriptionPlan", "price", "studentPrice", "seniorPrice", "handicapPrice"),
            VisitTableResponse.class, Set.of("id", "firstName", "lastName", "phone"),
            TrainingResponse.class, Set.of("name", "category", "date", "duration", "capacity", "spots", "trainer", "joined")
    );


    private static final Map<Class<?>, SortingConfigDto> sortingConfigMap = Map.of(
            MemberTableDto.class, new SortingConfigDto().setField("updatedAt").setDesc(true)
    );
}
