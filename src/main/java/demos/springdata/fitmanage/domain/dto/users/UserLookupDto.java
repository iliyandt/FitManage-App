package demos.springdata.fitmanage.domain.dto.users;

public class UserLookupDto {
    private String title;
    private String value;

    public UserLookupDto() {
    }

    public UserLookupDto(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public UserLookupDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getValue() {
        return value;
    }

    public UserLookupDto setValue(String value) {
        this.value = value;
        return this;
    }
}
