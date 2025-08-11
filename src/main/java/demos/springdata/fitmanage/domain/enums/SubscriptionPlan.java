package demos.springdata.fitmanage.domain.enums;

public enum SubscriptionPlan {
    VISIT_PASS(12, "Visit Pass"),
    MONTHLY(null, "Monthly"),
    DAY_PASS(null, "Day Pass"),
    WEEKLY_PASS(null, "Weekly Pass"),
    BIANNUAL(null, "Biannual"),
    ANNUAL(null, "Annual");
    private final Integer defaultVisits;
    private final String displayName;

    SubscriptionPlan(Integer defaultVisits, String displayName) {
        this.defaultVisits = defaultVisits;
        this.displayName = displayName;
    }

    public boolean isVisitBased() {
        return defaultVisits != null;
    }

    public boolean isTimeBased() {
        return !isVisitBased();
    }

    public Integer getDefaultVisits() {
        return defaultVisits;
    }

    public String getDisplayName() {
        return displayName;
    }
}
