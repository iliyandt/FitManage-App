package demos.springdata.fitmanage.domain.dto.employee;

import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class EmployeeTableDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private Set<RoleType> roles = new HashSet<>();
    private Employment employment;
    private OffsetDateTime birthDate;
    private String phone;
    private EmployeeRole employeeRole;


    public EmployeeTableDto() {
    }

    public Long getId() {
        return id;
    }

    public EmployeeTableDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public EmployeeTableDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public EmployeeTableDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public EmployeeTableDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public EmployeeTableDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Set<RoleType> getRoles() {
        return roles;
    }

    public EmployeeTableDto setRoles(Set<RoleType> roles) {
        this.roles = roles;
        return this;
    }

    public Employment getEmployment() {
        return employment;
    }

    public EmployeeTableDto setEmployment(Employment employment) {
        this.employment = employment;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public EmployeeTableDto setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public EmployeeTableDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    public EmployeeTableDto setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
        return this;
    }
}
