package tech.kayys.syirkah.foundation.application.middleware;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandContext;
import tech.kayys.syirkah.foundation.application.command.CommandInvocation;
import tech.kayys.syirkah.foundation.application.command.CommandMiddleware;
import tech.kayys.syirkah.foundation.application.query.QueryContext;
import tech.kayys.syirkah.foundation.application.query.QueryInvocation;
import tech.kayys.syirkah.foundation.application.query.QueryMiddleware;
import tech.kayys.syirkah.foundation.application.validation.Validatable;

/**
 * Middleware that executes validation for {@link Validatable} commands and queries before dispatching.
 */
public final class ValidationMiddleware implements CommandMiddleware, QueryMiddleware, OrderedMiddleware {

    @Override
    public int order() {
        return VALIDATION_ORDER;
    }

    @Override
    public <R> Uni<R> invoke(CommandContext context, CommandInvocation<R> next) {
        Object command = context != null ? context.command() : null;
        try {
            validateTarget(command);
        } catch (Throwable t) {
            return Uni.createFrom().failure(t);
        }
        return next.proceed();
    }

    @Override
    public <R> Uni<R> invoke(QueryContext context, QueryInvocation<R> next) {
        Object query = context != null ? context.query() : null;
        try {
            validateTarget(query);
        } catch (Throwable t) {
            return Uni.createFrom().failure(t);
        }
        return next.proceed();
    }

    private void validateTarget(Object target) {
        if (target instanceof Validatable v) {
            v.validate();
        }
    }
}
