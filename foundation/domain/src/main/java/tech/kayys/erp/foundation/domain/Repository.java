package tech.kayys.erp.foundation.domain;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Generic repository interface for aggregate roots.
 * Infrastructure implementations provide the actual persistence.
 * 
 * @param <A> The aggregate root type
 * @param <ID> The aggregate's identifier type
 */
public interface Repository<A extends AggregateRoot<ID>, ID extends Identifier<?>> {
    
    CompletionStage<A> save(A aggregate);
    
    CompletionStage<Optional<A>> findById(ID id);
    
    CompletionStage<Boolean> existsById(ID id);
    
    CompletionStage<Void> delete(A aggregate);
    
    CompletionStage<Void> deleteById(ID id);
}
