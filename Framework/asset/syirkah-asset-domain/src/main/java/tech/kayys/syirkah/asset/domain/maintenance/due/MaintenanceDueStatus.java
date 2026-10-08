package tech.kayys.syirkah.asset.domain.maintenance.due;

/** Due occurrence status (ASSET-22 §13). */
public enum MaintenanceDueStatus {
    UPCOMING,
    DUE,
    OVERDUE,
    COMPLETED,
    SKIPPED,
    CANCELLED
}
