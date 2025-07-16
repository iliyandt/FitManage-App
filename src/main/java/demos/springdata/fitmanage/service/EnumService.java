package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.common.EnumOption;

import java.util.List;

public interface EnumService {
    List<EnumOption> getEnumOptions(String enumName);
}
