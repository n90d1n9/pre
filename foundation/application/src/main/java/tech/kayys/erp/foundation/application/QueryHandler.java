package tech.kayys.erp.foundation.application;

import java.util.concurrent.CompletionStage;

/**
 * Handler for processing queries.
 * Returns a CompletionStage for reactive, non-blocking execution.
 * 
 * @param <Q> The query type this handler processes
 * @param <R> The result type returned by the query
 */
@FunctionalInterface
public interface QueryHandler<Q extends Query<R>, R> {
    
    CompletionStage<R> handle(Q query);
}
