package tech.kayys.erp.foundation.application.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void wrapsSuccessValue() {
        var result = Result.success("order-created");

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals("order-created", result.orElseThrow());
    }

    @Test
    void wrapsFailureError() {
        var error = ApplicationError.of("ORDER_NOT_FOUND", "Order does not exist");
        var result = Result.<String>failure(error);

        assertTrue(result.isFailure());

        var thrown = assertThrows(
                ApplicationErrorException.class,
                result::orElseThrow
        );

        assertEquals(error, thrown.error());
    }

    @Test
    void mapsSuccessValue() {
        var result = Result.success(2).map(value -> value * 10);

        assertEquals(20, result.orElseThrow());
    }

    @Test
    void mapPropagatesFailureUnchanged() {
        var error = ApplicationError.of("INVALID", "Invalid state");
        Result<Integer> result = Result.failure(error);

        var mapped = result.map(value -> value * 10);

        assertTrue(mapped.isFailure());
    }

}
