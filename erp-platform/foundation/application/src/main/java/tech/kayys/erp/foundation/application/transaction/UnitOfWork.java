package tech.kayys.erp.foundation.application.transaction;

import io.smallrye.mutiny.Uni;

import java.util.function.Supplier;

/**
 * Transaction boundary port.
 *
 * A command handler asks the unit of work to run its orchestration
 * atomically, without knowing whether the underlying implementation
 * is a JDBC/JPA transaction, a Hibernate Reactive session, or
 * something else entirely.
 */
public interface UnitOfWork {

    <R> Uni<R> execute(Supplier<Uni<R>> work);

}
