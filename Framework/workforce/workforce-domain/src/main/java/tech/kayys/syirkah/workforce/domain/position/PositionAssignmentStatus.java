package tech.kayys.syirkah.workforce.domain.position;

/**
 * Status of a position assignment.
 */
public enum PositionAssignmentStatus {
    ACTIVE,
    ENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
