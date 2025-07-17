package demos.springdata.fitmanage.domain.dto.team;

import demos.springdata.fitmanage.domain.enums.StaffRoleSelectionType;
import jakarta.validation.constraints.NotNull;

public class StaffRoleAssignment {
    @NotNull
    private StaffRoleSelectionType selectionType;

    private Long roleId;
    private String customRoleName;

    public StaffRoleAssignment() {
    }

    public StaffRoleSelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(StaffRoleSelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getCustomRoleName() {
        return customRoleName;
    }

    public void setCustomRoleName(String customRoleName) {
        this.customRoleName = customRoleName;
    }
}
