package demos.springdata.fitmanage.helper;

import java.util.Map;

@FunctionalInterface
public interface RowMapper<T> {
    Map<String, Object> mapRow(T dto);
}
