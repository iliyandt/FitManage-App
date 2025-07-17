package demos.springdata.fitmanage.domain.dto.team;

import demos.springdata.fitmanage.domain.entity.StaffRole;

public class StaffMemberTableDto {
    private Long id;
    private String firstName;
    private String lastName;
    //private StaffRole staffRole;
    private String phone;

    public StaffMemberTableDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

//    public StaffRole getStaffRole() {
//        return staffRole;
//    }
//
//    public void setStaffRole(StaffRole staffRole) {
//        this.staffRole = staffRole;
//    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

