package demos.springdata.fitmanage.domain.dto.shift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateShift {
    private Long id;
    private String name;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String notes;
}
