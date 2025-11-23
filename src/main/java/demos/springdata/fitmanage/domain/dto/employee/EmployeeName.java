package demos.springdata.fitmanage.domain.dto.employee;

import java.util.UUID;

public record EmployeeName(
        UUID id,
        String name
) {
}
