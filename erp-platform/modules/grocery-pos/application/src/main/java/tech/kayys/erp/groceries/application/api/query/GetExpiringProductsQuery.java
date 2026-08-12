package tech.kayys.erp.groceries.application.api.query;

import tech.kayys.erp.foundation.application.Query;

public record GetExpiringProductsQuery(int daysThreshold) implements Query<ExpiryListResult> {
    public GetExpiringProductsQuery {
        if (daysThreshold <= 0) throw new IllegalArgumentException("Days threshold must be positive");
    }
}
