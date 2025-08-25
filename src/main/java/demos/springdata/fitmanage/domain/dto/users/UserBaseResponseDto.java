package demos.springdata.fitmanage.domain.dto.users;

import demos.springdata.fitmanage.domain.enums.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;


public non-sealed class UserBaseResponseDto implements UserProfileDto {
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

    public UserBaseResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public UserBaseResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserBaseResponseDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserBaseResponseDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserBaseResponseDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserBaseResponseDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public UserBaseResponseDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Set<RoleType> getRoles() {
        return roles;
    }

    public UserBaseResponseDto setRoles(Set<RoleType> roles) {
        this.roles = roles;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public UserBaseResponseDto setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UserBaseResponseDto setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UserBaseResponseDto setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserBaseResponseDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserBaseResponseDto setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserBaseResponseDto setCity(String city) {
        this.city = city;
        return this;
    }
}
