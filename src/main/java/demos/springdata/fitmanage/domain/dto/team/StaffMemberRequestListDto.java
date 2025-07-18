package demos.springdata.fitmanage.domain.dto.team;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class StaffMemberRequestListDto {
    @NotEmpty(message = "Staff list must not be empty")
    @Valid
    private List<StaffMemberRequestDto> staff;

    public StaffMemberRequestListDto() {
    }

    public List<StaffMemberRequestDto> getStaff() {
        return staff;
    }

    public void setStaff(List<StaffMemberRequestDto> staff) {
        this.staff = staff;
    }
}
