package demos.springdata.fitmanage.domain.enums;

public enum EmployeeRole {
    MANAGER("Manager"),
    NUTRITIONIST("Nutritionist"),
    TRAINER("Trainer"),
    RECEPTIONIST("Receptionist"),
    CLEANER("Cleaner"),
    PHYSIOTHERAPIST("Physiotherapist");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
