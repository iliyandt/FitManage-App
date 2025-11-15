package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.common.response.EnumOption;
import org.springframework.http.HttpStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.service.EnumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnumServiceImpl implements EnumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumServiceImpl.class);
    private static final String ENUM_PACKAGE = "demos.springdata.fitmanage.domain.enums.";

    public EnumServiceImpl() {
    }

    @Override
    public List<EnumOption> getEnumOptions(String enumName) {
        LOGGER.info("Enum requested: {}", enumName);
        try {
            Class<?> clazz = Class.forName(ENUM_PACKAGE + enumName);
            LOGGER.info("Resolved enum class: {}", clazz);

            if (!clazz.isEnum()) {
                throw new DamilSoftException("Provided type is not an enum", HttpStatus.BAD_REQUEST);
            }

            return Arrays.stream(clazz.getEnumConstants())
                    .map(constant -> {
                        String value = constant.toString();
                        String title = capitalize(value.toLowerCase().replace("_", " "));
                        return new EnumOption(title, value);
                    })
                    .collect(Collectors.toList());

        } catch (ClassNotFoundException e) {
            LOGGER.error("Enum not found: {}", enumName, e);
            throw new DamilSoftException("Enum not found: " + enumName, HttpStatus.NOT_FOUND);
        }
    }

    private String capitalize(String input) {
        String[] words = input.split(" ");
        return Arrays.stream(words)
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(Collectors.joining(" "));
    }
}
