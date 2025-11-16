package demos.springdata.fitmanage.domain.dto.shift;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String role;
    private boolean approved;
    private String notes;
}