package tech.kayys.syirkah.billing.application.api.query;

import java.util.List;

public record BillingHistoryView(
        String customerId,
        List<BillingScheduleView> schedules
) {}
