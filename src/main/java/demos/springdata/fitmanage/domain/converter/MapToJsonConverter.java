package demos.springdata.fitmanage.domain.converter;

import com.google.gson.Gson;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter(autoApply = true)
public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Gson gson = new Gson();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        return attribute == null ? null : gson.toJson(attribute);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        return dbData == null ? null : gson.fromJson(dbData, Map.class);
    }
}
