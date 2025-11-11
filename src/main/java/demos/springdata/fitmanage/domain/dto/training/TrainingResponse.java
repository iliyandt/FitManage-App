package demos.springdata.fitmanage.domain.dto.training;

import java.time.Instant;
import java.util.Set;

public record TrainingResponse(
        String title,
        String category,
        Instant date,
        Integer duration,
        Integer capacity,
        Integer spots,
        String trainer,
        Boolean joined
) {
}
