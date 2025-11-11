package demos.springdata.fitmanage.domain.dto.training;

import java.time.Instant;

public record TrainingRequest
        (
                Long trainerId,
                String name,
                String category,
                Instant date,
                Integer duration,
                Integer capacity
        ) {
}
