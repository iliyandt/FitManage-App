package demos.springdata.fitmanage.domain.enums;

public enum Permission {
        // Member management
        VIEW_MEMBERS,
        EDIT_MEMBERS,
        DELETE_MEMBERS,

        // Schedule management
        VIEW_SCHEDULES,
        EDIT_SCHEDULES,
        MANAGE_CLASSES,

        // Equipment management
        VIEW_EQUIPMENT,
        MANAGE_EQUIPMENT,

        // Reports and analytics
        VIEW_REPORTS,
        GENERATE_REPORTS,

        // Staff management (admin only)
        MANAGE_STAFF,

        // Payment management
        VIEW_PAYMENTS,
        MANAGE_PAYMENTS,

        // General gym operations
        ACCESS_RECEPTION,
        MANAGE_BOOKINGS
}
