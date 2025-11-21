package demos.springdata.fitmanage.domain.dto.visit;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitDto {
    private Long userId;
    private Long membershipId;
    private LocalDateTime checkInAt;
}
