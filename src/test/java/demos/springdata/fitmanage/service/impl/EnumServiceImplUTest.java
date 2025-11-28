package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.common.response.EnumOption;
import demos.springdata.fitmanage.exception.DamilSoftException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EnumServiceImplUTest {

    @InjectMocks
    private EnumServiceImpl enumService;

    @BeforeEach
    void setUp() {
        enumService = new EnumServiceImpl();
    }

    @Test
    void getEnumOptions_ShouldReturnFormattedList_WhenEnumExists() {
        String enumName = "Gender";

        List<EnumOption> result = enumService.getEnumOptions(enumName);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        boolean hasMale = result.stream()
                .anyMatch(opt -> opt.value().equals("MALE") && opt.title().equals("Male"));

        assertTrue(hasMale, "Should contain MALE option formatted as 'Male'");
    }

    @Test
    void getEnumOptions_ShouldHandleUnderscoresCorrectly_WhenEnumHasComplexNames() {
        String enumName = "Employment";

        List<EnumOption> result = enumService.getEnumOptions(enumName);

        assertNotNull(result);


        result.stream()
                .filter(opt -> opt.value().contains("_"))
                .findFirst()
                .ifPresent(opt -> {
                    String value = opt.value();
                    String title = opt.title();

                    assertFalse(title.contains("_"), "Title should not contain underscores");
                    assertTrue(Character.isUpperCase(title.charAt(0)), "Title should start with uppercase");
                    assertTrue(title.contains(" "), "Title should have spaces instead of underscores");
                });
    }

    @Test
    void getEnumOptions_ShouldThrowException_WhenEnumNotFound() {

        String nonExistentEnum = "NonExistentEnum123";

        DamilSoftException exception = assertThrows(DamilSoftException.class, () -> {
            enumService.getEnumOptions(nonExistentEnum);
        });

        assertEquals("Enum not found: " + nonExistentEnum, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode());
    }
}