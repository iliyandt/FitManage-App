package demos.springdata.fitmanage.domain.dto.training;

import java.time.LocalDateTime;
import java.util.UUID;

public record TrainingRequest
        (
                UUID trainer,
                String title,
                String category,
                String location,
                LocalDateTime date,
                Integer duration,
                Integer capacity
        ) {
}
