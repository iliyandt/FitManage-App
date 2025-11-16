package demos.springdata.fitmanage.domain.dto.training;

import demos.springdata.fitmanage.annotation.DropDown;
import java.time.LocalDateTime;

public record TrainingResponse(
        Long id,
        String title,
        String category,
        String location,
        LocalDateTime date,
        Integer duration,
        Integer capacity,
        Integer spots,
        @DropDown(url = "employees/role")
        String trainer,
        Boolean joined
) {
}
