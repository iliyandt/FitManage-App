package demos.springdata.fitmanage.domain.dto.training;

import java.time.Instant;

public record TrainingRequest
        (
                Long trainer,
                String title,
                String category,
                Instant date,
                Integer duration,
                Integer capacity
        ) {
}
