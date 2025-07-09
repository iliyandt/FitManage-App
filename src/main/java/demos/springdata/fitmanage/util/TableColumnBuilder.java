package demos.springdata.fitmanage.util;

import demos.springdata.fitmanage.domain.dto.ColumnConfigDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableDto;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TableColumnBuilder {

    public static List<ColumnConfigDto> buildColumns(Class<GymMemberTableDto> gymMemberTableDtoClass) {
        return Arrays.stream(GymMemberTableDto.class.getDeclaredFields())
                .map(field -> new ColumnConfigDto(
                        field.getName(),
                        beautifyColumnName(field.getName()),
                        mapJavaTypeToFrontendType(field.getType())
                ))
                .toList();
    }

    private static String mapJavaTypeToFrontendType(Class<?> type) {
        if (type == String.class) {
            return "string";
        } else if (type == Integer.class || type == int.class ||
                type == Long.class || type == long.class ||
                type == Double.class || type == double.class ||
                type == Float.class || type == float.class) {
            return "number";
        } else if (type == Boolean.class || type == boolean.class) {
            return "boolean";
        } else if (type.getName().contains("LocalDate") || type.getName().contains("Date")) {
            return "date";
        } else {
            return "string";
        }
    }

    private static String beautifyColumnName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) return fieldName;

        String withSpaces = fieldName.replaceAll("([a-z])([A-Z])", "$1 $2");

        return Pattern.compile("\\b\\w")
                .matcher(withSpaces)
                .replaceAll(match -> match.group().toUpperCase())
                .trim();
    }
}
