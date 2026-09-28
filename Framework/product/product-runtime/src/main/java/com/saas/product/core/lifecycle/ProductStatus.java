package com.saas.product.core.lifecycle;

/**
 * Product lifecycle states.
 *
 * Allowed transitions:
 *
 *   DRAFT ──────► ACTIVE ──────► ARCHIVED
 *     │                              ▲
 *     └──────────────────────────────┘  (can archive from draft directly)
 *
 *   ACTIVE ──────► SUSPENDED (temp unavailable, reactivatable)
 *   SUSPENDED ──► ACTIVE
 *   SUSPENDED ──► ARCHIVED
 */
public enum ProductStatus {

    /**
     * Created but not yet published. Not visible to customers.
     * All editing operations are allowed.
     */
    DRAFT,

    /**
     * Published and available for sale/ordering.
     */
    ACTIVE,

    /**
     * Temporarily hidden from storefront. Retains all data.
     * Can return to ACTIVE.
     */
    SUSPENDED,

    /**
     * Permanently retired. Cannot be ordered. Read-only.
     */
    ARCHIVED;

    public boolean canTransitionTo(ProductStatus target) {
        return switch (this) {
            case DRAFT      -> target == ACTIVE   || target == ARCHIVED;
            case ACTIVE     -> target == SUSPENDED || target == ARCHIVED;
            case SUSPENDED  -> target == ACTIVE   || target == ARCHIVED;
            case ARCHIVED   -> false;  // terminal state
        };
    }
}
