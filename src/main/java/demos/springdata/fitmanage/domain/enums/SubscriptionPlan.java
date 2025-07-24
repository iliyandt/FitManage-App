package demos.springdata.fitmanage.domain.enums;

public enum SubscriptionPlan {
    VISIT_PASS(12),
    MONTHLY,
    DAY_PASS,
    WEEKLY_PASS,
    BIANNUAL,
    ANNUAL;

    private final Integer defaultVisits;

    SubscriptionPlan() {
        this.defaultVisits = null;
    }

    SubscriptionPlan(Integer defaultVisits) {
        this.defaultVisits = defaultVisits;
    }

    public boolean isVisitBased() {
        return defaultVisits != null;
    }

    public Integer getDefaultVisits() {
        return defaultVisits;
    }
}
