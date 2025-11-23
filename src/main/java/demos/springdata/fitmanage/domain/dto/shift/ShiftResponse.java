package demos.springdata.fitmanage.domain.dto.shift;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String role;
    private boolean approved;
    private String notes;
}