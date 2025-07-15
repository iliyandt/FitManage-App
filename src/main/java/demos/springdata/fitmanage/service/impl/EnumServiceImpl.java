package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.EnumOption;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.EnumService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnumServiceImpl implements EnumService {

    private static final String ENUM_PACKAGE = "demos.springdata.fitmanage.domain.enums.";

    @Override
    public List<EnumOption> getEnumOptions(String enumName) {
        try {
            Class<?> clazz = Class.forName(ENUM_PACKAGE + enumName);

            if (!clazz.isEnum()) {
                throw new FitManageAppException("Provided type is not an enum", ApiErrorCode.BAD_REQUEST);
            }

            return Arrays.stream(clazz.getEnumConstants())
                    .map(constant -> {
                        String value = constant.toString();
                        String title = capitalize(value.toLowerCase().replace("_", " "));
                        return new EnumOption(title, value);
                    })
                    .collect(Collectors.toList());

        } catch (ClassNotFoundException e) {
            throw new FitManageAppException("Enum not found: " + enumName, ApiErrorCode.NOT_FOUND);
        }
    }

    private String capitalize(String input) {
        String[] words = input.split(" ");
        return Arrays.stream(words)
                .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                .collect(Collectors.joining(" "));
    }
}
