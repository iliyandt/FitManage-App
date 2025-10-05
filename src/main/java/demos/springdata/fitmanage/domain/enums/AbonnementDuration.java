package demos.springdata.fitmanage.domain.enums;

public enum AbonnementDuration {
    MONTHLY("monthly"),
    ANNUALLY("annually");

    private final String displayName;

    AbonnementDuration(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
