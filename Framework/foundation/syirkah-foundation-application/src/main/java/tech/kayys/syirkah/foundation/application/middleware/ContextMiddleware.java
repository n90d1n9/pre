package tech.kayys.syirkah.foundation.application.middleware;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandContext;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.command.CommandInvocation;
import tech.kayys.syirkah.foundation.application.command.CommandMiddleware;
import tech.kayys.syirkah.foundation.application.context.CurrentExecutionContext;
import tech.kayys.syirkah.foundation.application.context.ExecutionContext;
import tech.kayys.syirkah.foundation.application.query.QueryContext;
import tech.kayys.syirkah.foundation.application.query.QueryInvocation;
import tech.kayys.syirkah.foundation.application.query.QueryMiddleware;

/**
 * Middleware that sets the ambient {@link CurrentExecutionContext} for the duration
 * of a command or query dispatch pipeline.
 */
public final class ContextMiddleware implements CommandMiddleware, QueryMiddleware, OrderedMiddleware {

    @Override
    public int order() {
        return CONTEXT_ORDER;
    }

    @Override
    public <R> Uni<R> invoke(CommandContext context, CommandInvocation<R> next) {
        ExecutionContext execContext = context != null ? context.executionContext() : ExecutionContext.empty();
        ExecutionContext previous = CurrentExecutionContext.get().orElse(null);

        CurrentExecutionContext.set(execContext);
        return next.proceed()
                .eventually(() -> CurrentExecutionContext.set(previous));
    }

    @Override
    public <R> Uni<R> invoke(QueryContext context, QueryInvocation<R> next) {
        ExecutionContext execContext = context != null ? context.executionContext() : ExecutionContext.empty();
        ExecutionContext previous = CurrentExecutionContext.get().orElse(null);

        CurrentExecutionContext.set(execContext);
        return next.proceed()
                .eventually(() -> CurrentExecutionContext.set(previous));
    }
}
