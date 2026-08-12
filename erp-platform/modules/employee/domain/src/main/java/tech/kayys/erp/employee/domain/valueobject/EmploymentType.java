package tech.kayys.erp.employee.domain.valueobject;

/**
 * Type of employment.
 */
public enum EmploymentType {
    FULL_TIME("Full Time"),
    PART_TIME("Part Time"),
    CONTRACT("Contract"),
    INTERN("Intern"),
    APPRENTICE("Apprentice"),
    FREELANCE("Freelance"),
    CONSULTANT("Consultant"),
    TEMPORARY("Temporary");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPermanent() {
        return this == FULL_TIME || this == PART_TIME;
    }

    public boolean isContractual() {
        return this == CONTRACT || this == FREELANCE || 
               this == CONSULTANT || this == TEMPORARY;
    }
}