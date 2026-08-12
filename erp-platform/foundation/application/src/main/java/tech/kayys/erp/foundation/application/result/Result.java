package tech.kayys.erp.foundation.application.result;

import java.util.Objects;
import java.util.function.Function;

/**
 * Explicit success/failure outcome for a use case, instead of relying
 * on exceptions for expected/business failures.
 *
 * Kept intentionally small - this is not a general-purpose functional
 * "Either" library, just enough to let a CommandHandler / QueryHandler
 * return a typed outcome.
 *
 * @param <T> the success value type
 */
public sealed interface Result<T> permits Result.Success, Result.Failure {

    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Result<T> failure(ApplicationError error) {
        return new Failure<>(error);
    }

    boolean isSuccess();

    default boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Returns the success value, or throws {@link ApplicationErrorException}
     * if this is a failure.
     */
    T orElseThrow();

    <R> Result<R> map(Function<T, R> mapper);

    record Success<T>(T value) implements Result<T> {

        public Success {
            Objects.requireNonNull(value, "value cannot be null");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T orElseThrow() {
            return value;
        }

        @Override
        public <R> Result<R> map(Function<T, R> mapper) {
            Objects.requireNonNull(mapper, "mapper cannot be null");
            return new Success<>(mapper.apply(value));
        }

    }

    record Failure<T>(ApplicationError error) implements Result<T> {

        public Failure {
            Objects.requireNonNull(error, "error cannot be null");
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T orElseThrow() {
            throw error.toException();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> Result<R> map(Function<T, R> mapper) {
            // A failure carries no value to map - propagate unchanged.
            return (Result<R>) this;
        }

    }

}
