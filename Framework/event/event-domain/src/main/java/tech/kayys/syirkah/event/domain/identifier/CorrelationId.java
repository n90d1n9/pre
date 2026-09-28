package tech.kayys.syirkah.event.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.Objects;

/**
 * Ties together every event produced while handling one business request.
 *
 * <p>Deliberately a plain string rather than a UUID: correlation ids often
 * originate outside Syirkah (an HTTP header, a partner's message id) and
 * must survive round trips unchanged.
 */
public final class CorrelationId extends Identifier<String> {

    private static final long serialVersionUID = 1L;

    public CorrelationId(String value) {
        super(requireText(value));
    }

    public static CorrelationId of(String value) {
        return new CorrelationId(value);
    }

    public static CorrelationId generate() {
        return new CorrelationId(java.util.UUID.randomUUID().toString());
    }

    private static String requireText(String value) {
        Objects.requireNonNull(value, "CorrelationId cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CorrelationId cannot be blank");
        }
        return value;
    }

    @Override
    public String toString() {
        return "CorrelationId{" + value + "}";
    }
}
