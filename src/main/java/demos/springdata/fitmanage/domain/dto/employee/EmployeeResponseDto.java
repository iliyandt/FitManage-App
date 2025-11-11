package demos.springdata.fitmanage.domain.dto.employee;

import demos.springdata.fitmanage.domain.enums.EmployeeRole;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class EmployeeResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Gender gender;
    private Set<RoleType> roles = new HashSet<>();
    private OffsetDateTime birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String phone;
    private String address;
    private String city;
    private EmployeeRole employeeRole;

    public EmployeeResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public EmployeeResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public EmployeeResponseDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public EmployeeResponseDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public EmployeeResponseDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public EmployeeResponseDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public EmployeeResponseDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Set<RoleType> getRoles() {
        return roles;
    }

    public EmployeeResponseDto setRoles(Set<RoleType> roles) {
        this.roles = roles;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public EmployeeResponseDto setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public EmployeeResponseDto setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public EmployeeResponseDto setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public EmployeeResponseDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public EmployeeResponseDto setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public EmployeeResponseDto setCity(String city) {
        this.city = city;
        return this;
    }


    public EmployeeRole getEmployeeRole() {
        return employeeRole;
    }

    public EmployeeResponseDto setEmployeeRole(EmployeeRole employeeRole) {
        this.employeeRole = employeeRole;
        return this;
    }
}
