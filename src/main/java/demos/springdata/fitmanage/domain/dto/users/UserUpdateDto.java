package demos.springdata.fitmanage.domain.dto.users;


import demos.springdata.fitmanage.domain.enums.Gender;

public class UserUpdateDto {
    private String phone;
    private String address;
    private String city;
    private Gender gender;

    public UserUpdateDto() {}

    public UserUpdateDto(String phone, String address, String city) {
        this.phone = phone;
        this.address = address;
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Gender getGender() {
        return gender;
    }

    public UserUpdateDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }
}
