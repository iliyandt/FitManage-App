package demos.springdata.fitmanage.domain.dto.team;

import demos.springdata.fitmanage.domain.enums.StaffRoleSelectionType;

public class StaffRoleOptionDto {
    private String title;
    private String value;
    private StaffRoleSelectionType selectionType;
    private String description;


    public StaffRoleOptionDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StaffRoleSelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(StaffRoleSelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
