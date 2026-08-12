package tech.kayys.erp.tenant.domain.valueobject;

/**
 * User role types within a tenant.
 */
public enum RoleType {
    SUPER_ADMIN("Super Admin - full system access"),
    TENANT_ADMIN("Tenant Admin - tenant-level administration"),
    COMPANY_ADMIN("Company Admin - company-level administration"),
    MANAGER("Manager - team management"),
    USER("User - standard user"),
    READ_ONLY("Read Only - view-only access"),
    ACCOUNTANT("Accountant - financial access"),
    HR_ADMIN("HR Admin - human resources access"),
    SALES_REP("Sales Rep - sales access"),
    CUSTOMER("Customer - customer portal access");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdmin() {
        return this == SUPER_ADMIN || this == TENANT_ADMIN || this == COMPANY_ADMIN;
    }

    public boolean hasFullAccess() {
        return this == SUPER_ADMIN || this == TENANT_ADMIN;
    }
}