package tech.kayys.erp.foundation.application;

import java.util.concurrent.CompletionStage;

/**
 * Handler for processing commands.
 * Returns a CompletionStage for reactive, non-blocking execution.
 * 
 * @param <C> The command type this handler processes
 * @param <R> The result type returned by processing the command
 */
@FunctionalInterface
public interface CommandHandler<C extends Command<R>, R> {
    
    CompletionStage<R> handle(C command);
}
