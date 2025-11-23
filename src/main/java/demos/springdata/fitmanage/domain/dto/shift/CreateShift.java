package demos.springdata.fitmanage.domain.dto.shift;

import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateShift {
    private UUID id;
    private String name;
    @FutureOrPresent(message = "Start time should not be in the past.")
    private OffsetDateTime startTime;
    @FutureOrPresent(message = "Start time should not be in the past.")
    private OffsetDateTime endTime;
    private String notes;
}
