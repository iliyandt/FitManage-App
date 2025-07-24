package demos.springdata.fitmanage.domain.dto.team.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class StaffMemberRequestListDto {
    @NotEmpty(message = "Staff list must not be empty")
    @Valid
    private List<StaffMemberCreateRequestDto> staff;

    public StaffMemberRequestListDto() {
    }

    public List<StaffMemberCreateRequestDto> getStaff() {
        return staff;
    }

    public void setStaff(List<StaffMemberCreateRequestDto> staff) {
        this.staff = staff;
    }
}
