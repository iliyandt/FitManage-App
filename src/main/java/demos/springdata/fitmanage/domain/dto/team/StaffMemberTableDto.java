package demos.springdata.fitmanage.domain.dto.team;

import demos.springdata.fitmanage.domain.enums.StaffPosition;

public class StaffMemberTableDto {
    private Long id;
    private String fullName;
    private StaffPosition staffPosition;
    private String phone;

    public StaffMemberTableDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public StaffPosition getStaffPosition() {
        return staffPosition;
    }

    public void setStaffPosition(StaffPosition staffPosition) {
        this.staffPosition = staffPosition;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

