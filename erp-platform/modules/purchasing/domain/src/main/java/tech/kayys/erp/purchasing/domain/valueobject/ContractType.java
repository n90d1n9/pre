package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Types of vendor contracts.
 */
public enum ContractType {
    SUPPLY_AGREEMENT("Supply Agreement"),
    SERVICE_AGREEMENT("Service Agreement"),
    MASTER_SERVICE_AGREEMENT("Master Service Agreement"),
    STATEMENT_OF_WORK("Statement of Work"),
    NON_DISCLOSURE_AGREEMENT("Non-Disclosure Agreement"),
    PURCHASE_AGREEMENT("Purchase Agreement"),
    FRAMEWORK_AGREEMENT("Framework Agreement"),
    LICENSE_AGREEMENT("License Agreement");

    private final String displayName;

    ContractType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}