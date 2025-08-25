package demos.springdata.fitmanage.util;

import demos.springdata.fitmanage.annotation.DropDown;
import demos.springdata.fitmanage.domain.dto.common.config.ColumnConfigDto;
import demos.springdata.fitmanage.domain.dto.common.config.DropDownConfig;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class TableColumnBuilder {

    public static <T> List<ColumnConfigDto> buildColumns(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> {
                    String fieldName = field.getName();
                    String header = beautifyColumnName(fieldName);
                    DropDownConfig dropDownConfig = resolveDropDownConfig(field);;
                    String type = resolveDropDownType(field, dropDownConfig);

                    return new ColumnConfigDto(fieldName, header, type, dropDownConfig);
                })
                .toList();
    }

    private static String resolveDropDownType(Field field, DropDownConfig dropDownConfig) {
        if (dropDownConfig != null && dropDownConfig.isFromAnnotation()) {
            return "dropdown";
        }

        if (field.getType().isEnum()) {
            return "enum";
        }

        return mapJavaTypeToFrontendType(field.getType());
    }

    private static DropDownConfig resolveDropDownConfig(Field field) {
        if (field.isAnnotationPresent(DropDown.class)) {
            DropDown dropDown = field.getAnnotation(DropDown.class);
            if (dropDown != null) {
                return new DropDownConfig(dropDown.url(), true);
            }
        }

        if (field.getType().isEnum()) {
            String capitalizedFieldName = capitalizeFirstLetter(field.getName());
            String url = "/v1/" + capitalizedFieldName + "/values";
            return new DropDownConfig(url, false);
        }

        return null;
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }


    private static String mapJavaTypeToFrontendType(Class<?> type) {
        if (type == String.class) {
            return "string";
        } else if (type == Integer.class || type == int.class ||
                type == Long.class || type == long.class ||
                type == Double.class || type == double.class ||
                type == Float.class || type == float.class ||
                type == BigDecimal.class) {
            return "number";
        } else if (type == Boolean.class || type == boolean.class) {
            return "boolean";
        } else if (type.getName().contains("LocalDate") || type.getName().contains("Date")) {
            return "date";
        } else if (type.isEnum()) {
            return "enum";
        } else if (type == Set.class){
            return "array";
        } else {
            return "object";
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
