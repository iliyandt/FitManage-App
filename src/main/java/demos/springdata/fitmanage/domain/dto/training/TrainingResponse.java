package demos.springdata.fitmanage.domain.dto.training;

import demos.springdata.fitmanage.annotation.DropDown;
import java.time.Instant;

public record TrainingResponse(
        Long id,
        String title,
        String category,
        String location,
        Instant date,
        Integer duration,
        Integer capacity,
        Integer spots,
        @DropDown(url = "employees/role")
        String trainer,
        Boolean joined
) {
}
