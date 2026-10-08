package tech.kayys.syirkah.foundation.application.middleware;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandContext;
import tech.kayys.syirkah.foundation.application.command.CommandInvocation;
import tech.kayys.syirkah.foundation.application.command.CommandMiddleware;
import tech.kayys.syirkah.foundation.application.result.ApplicationErrors;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.domain.exception.BusinessRuleViolation;

import java.util.function.Function;

/**
 * Middleware that handles and classifies exceptions thrown during command execution (enhance03.md §18).
 */
public final class ErrorHandlingCommandMiddleware implements CommandMiddleware, OrderedMiddleware {

    private final Function<Throwable, Throwable> exceptionClassifier;

    public ErrorHandlingCommandMiddleware() {
        this(Function.identity());
    }

    public ErrorHandlingCommandMiddleware(Function<Throwable, Throwable> exceptionClassifier) {
        this.exceptionClassifier = exceptionClassifier != null ? exceptionClassifier : Function.identity();
    }

    @Override
    public int order() {
        return ERROR_ORDER;
    }

    @Override
    public <R> Uni<R> invoke(CommandContext context, CommandInvocation<R> next) {
        return next.proceed()
                .onFailure()
                .transform(exceptionClassifier);
    }
}
