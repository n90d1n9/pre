package tech.kayys.syirkah.workforce.domain.position;

/**
 * Lifecycle status of an organizational position.
 */
public enum PositionStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
