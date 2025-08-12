package demos.springdata.fitmanage.helper;

import demos.springdata.fitmanage.domain.dto.common.config.ColumnsLayoutConfigDto;
import demos.springdata.fitmanage.domain.dto.common.config.ConfigDto;
import demos.springdata.fitmanage.domain.dto.common.config.SortingConfigDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TableHelperTest {

    private final TableHelper helper = new TableHelper();

    static class SimpleDto {
        public Long id;
        public String name;
        public Integer age;
    }

    @Test
    void buildRowMap_populatesValues_andReplacesNullsWithNA() {
        SimpleDto dto = new SimpleDto();
        dto.id = 1L;
        dto.name = null; // should become "N/A"
        dto.age = 30;

        Map<String, Object> row = helper.buildRowMap(dto);
        assertEquals(1L, row.get("id"));
        assertEquals("N/A", row.get("name"));
        assertEquals(30, row.get("age"));
    }

    @Test
    void buildTableConfig_containsExpectedCreateFieldsAndVisibility_forMemberPlanPriceDto() {
        ConfigDto config = helper.buildTableConfig("/plan-prices", MemberPlanPriceDto.class);

        // Verify createFields include the specified allowed ones
        Map<String, Boolean> createFields = config.getCreateFields();
        assertNotNull(createFields);
        // Expected allowed fields from TableHelper.customCreateFieldMap
        List<String> expectedAllowed = List.of("subscriptionPlan", "price", "studentPrice", "seniorPrice", "handicapPrice", "currency");
        for (String key : expectedAllowed) {
            assertTrue(createFields.containsKey(key), "createFields should contain key: " + key);
            assertTrue(createFields.get(key), "createFields for key should be true: " + key);
        }

        // Verify column visibility map present (content depends on declared fields)
        ColumnsLayoutConfigDto columnsLayout = config.getColumnsLayoutConfig();
        assertNotNull(columnsLayout);
        Map<String, Boolean> visibility = columnsLayout.getColumnVisibility();
        assertNotNull(visibility);
        // At least these keys from TableHelper.customColumnVisibilityMap must be present and true
        assertEquals(Boolean.TRUE, visibility.get("id"));
        assertEquals(Boolean.TRUE, visibility.get("subscriptionPlan"));
        assertEquals(Boolean.TRUE, visibility.get("price"));
        assertEquals(Boolean.TRUE, visibility.get("currency"));

        // Sorting config default for MemberPlanPriceDto falls back to id desc
        SortingConfigDto sorting = config.getSortable();
        assertNotNull(sorting);
        assertEquals("id", sorting.getField());
        assertTrue(sorting.isDesc());
    }

    @Test
    void buildRows_mapsUsingProvidedMapper() {
        MemberPlanPriceDto dto = new MemberPlanPriceDto()
                .setId(10L)
                .setPrice(BigDecimal.TEN)
                .setCurrency("EUR");

        List<Map<String, Object>> rows = helper.buildRows(List.of(dto), helper::buildRowMap);
        assertEquals(1, rows.size());
        assertEquals(10L, rows.get(0).get("id"));
    }
}
