package tech.kayys.erp.employee.domain.valueobject;

/**
 * Employment status of an employee.
 */
public enum EmploymentStatus {
    ACTIVE("Active - currently employed"),
    PROBATION("Probation - trial period"),
    TERMINATED("Terminated - employment ended"),
    RESIGNED("Resigned - voluntarily left"),
    RETIRED("Retired - retired from employment"),
    ON_LEAVE("On Leave - temporarily away"),
    SUSPENDED("Suspended - temporarily inactive"),
    CONTRACT_END("Contract End - fixed term ended"),
    DECEASED("Deceased - passed away");

    private final String description;

    EmploymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PROBATION || this == ON_LEAVE;
    }

    public boolean isTerminal() {
        return this == TERMINATED || this == RESIGNED || 
               this == RETIRED || this == DECEASED || this == CONTRACT_END;
    }

    public boolean canTransitionTo(EmploymentStatus target) {
        return switch (this) {
            case ACTIVE -> target == PROBATION || target == ON_LEAVE || 
                           target == TERMINATED || target == RESIGNED || target == RETIRED;
            case PROBATION -> target == ACTIVE || target == TERMINATED;
            case ON_LEAVE -> target == ACTIVE || target == TERMINATED || target == RESIGNED;
            case SUSPENDED -> target == ACTIVE || target == TERMINATED;
            default -> false;
        };
    }
}