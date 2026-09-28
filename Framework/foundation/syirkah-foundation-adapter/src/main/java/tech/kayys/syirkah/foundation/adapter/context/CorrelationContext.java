package tech.kayys.syirkah.foundation.adapter.context;

import java.util.UUID;

/**
 * Holder for distributed tracing and request correlation ID.
 */
public final class CorrelationContext {

    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    private static final ThreadLocal<String> CURRENT_CORRELATION_ID = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static String getCorrelationId() {
        String id = CURRENT_CORRELATION_ID.get();
        if (id == null) {
            id = UUID.randomUUID().toString();
            CURRENT_CORRELATION_ID.set(id);
        }
        return id;
    }

    public static void setCorrelationId(String correlationId) {
        CURRENT_CORRELATION_ID.set(correlationId);
    }

    public static void clear() {
        CURRENT_CORRELATION_ID.remove();
    }
}
