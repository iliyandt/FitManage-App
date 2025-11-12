package demos.springdata.fitmanage.domain.dto.training;

import java.time.LocalDateTime;

public record TrainingRequest
        (
                Long trainer,
                String title,
                String category,
                String location,
                LocalDateTime date,
                Integer duration,
                Integer capacity
        ) {
}
