package demos.springdata.fitmanage.domain.dto.training;

import demos.springdata.fitmanage.annotation.DropDown;
import java.time.Instant;

public record TrainingResponse(
        String title,
        String category,
        Instant date,
        Integer duration,
        Integer capacity,
        Integer spots,
        @DropDown(url = "employees/role")
        String trainerId,
        Boolean joined
) {
}
