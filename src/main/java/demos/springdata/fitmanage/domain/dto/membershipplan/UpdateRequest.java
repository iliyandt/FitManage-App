package demos.springdata.fitmanage.domain.dto.membershipplan;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequest {
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal studentPrice;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal seniorPrice;
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal handicapPrice;
}
