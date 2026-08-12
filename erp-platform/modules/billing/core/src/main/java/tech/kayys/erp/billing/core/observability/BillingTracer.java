package tech.kayys.erp.billing.core.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

/**
 * Billing tracing for distributed tracing.
 */
@ApplicationScoped
public class BillingTracer {

    @Inject
    OpenTelemetry openTelemetry;

    private Tracer tracer;

    public void initialize() {
        tracer = openTelemetry.getTracer("billing");
    }

    /**
     * Starts a new trace span for a billing operation.
     */
    public Span startSpan(String operationName) {
        return tracer.spanBuilder(operationName).startSpan();
    }

    /**
     * Starts a new trace span with attributes.
     */
    public Span startSpan(String operationName, Map<String, String> attributes) {
        Span span = tracer.spanBuilder(operationName).startSpan();
        attributes.forEach(span::setAttribute);
        return span;
    }

    /**
     * Records a billing event with trace.
     */
    public void recordEvent(Span span, String eventName, String eventDetails) {
        span.addEvent(eventName, Map.of(
            "details", eventDetails,
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }

    /**
     * Ends a trace span.
     */
    public void endSpan(Span span) {
        span.end();
    }

    /**
     * Executes a traceable operation.
     */
    public <T> T traceOperation(String operationName, java.util.function.Supplier<T> operation) {
        Span span = tracer.spanBuilder(operationName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            return operation.get();
        } finally {
            span.end();
        }
    }

    /**
     * Executes a traceable asynchronous operation.
     */
    public <T> CompletionStage<T> traceAsyncOperation(
            String operationName,
            java.util.function.Supplier<CompletionStage<T>> operation) {
        
        Span span = tracer.spanBuilder(operationName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            return operation.get()
                .whenComplete((result, error) -> {
                    if (error != null) {
                        span.recordException(error);
                    }
                    span.end();
                });
        }
    }
}