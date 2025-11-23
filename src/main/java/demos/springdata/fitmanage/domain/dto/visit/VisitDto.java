package demos.springdata.fitmanage.domain.dto.visit;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitDto {
    private UUID userId;
    private UUID membershipId;
    private LocalDateTime checkInAt;
}
