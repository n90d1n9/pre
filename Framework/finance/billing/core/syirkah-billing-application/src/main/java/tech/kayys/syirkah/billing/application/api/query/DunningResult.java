package tech.kayys.syirkah.billing.application.api.query;

public record DunningResult(
        int totalProcessed,
        int successful,
        int failed,
        String message
) {}
