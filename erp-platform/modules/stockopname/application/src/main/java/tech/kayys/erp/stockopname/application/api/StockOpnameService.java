package tech.kayys.erp.stockopname.application.api;

import tech.kayys.erp.stockopname.application.api.command.*;
import tech.kayys.erp.stockopname.application.api.query.CountingSessionView;
import tech.kayys.erp.stockopname.application.api.query.CountingItemView;
import tech.kayys.erp.stockopname.application.api.query.VarianceReportView;
import tech.kayys.erp.stockopname.domain.identifier.CountingSessionId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for stock opname operations.
 */
public interface StockOpnameService {

    // ============ Counting Session Commands ============

    /**
     * Creates a new counting session.
     */
    CompletionStage<CountingSessionId> createCountingSession(CreateCountingSessionCommand command);

    /**
     * Starts a counting session.
     */
    CompletionStage<CountingSessionId> startCountingSession(StartCountingSessionCommand command);

    /**
     * Records a count for an item.
     */
    CompletionStage<CountingSessionId> recordCount(RecordCountCommand command);

    /**
     * Verifies a counted item.
     */
    CompletionStage<CountingSessionId> verifyCount(VerifyCountCommand command);

    /**
     * Verifies the entire counting session.
     */
    CompletionStage<CountingSessionId> verifySession(VerifySessionCommand command);

    /**
     * Applies adjustments for a verified session.
     */
    CompletionStage<CountingSessionId> applyAdjustments(ApplyAdjustmentsCommand command);

    /**
     * Reopens a completed session.
     */
    CompletionStage<CountingSessionId> reopenSession(ReopenSessionCommand command);

    /**
     * Cancels a counting session.
     */
    CompletionStage<CountingSessionId> cancelSession(CancelSessionCommand command);

    // ============ Queries ============

    /**
     * Gets a counting session by ID.
     */
    CompletionStage<CountingSessionView> getCountingSession(CountingSessionId sessionId);

    /**
     * Gets counting sessions by status.
     */
    CompletionStage<List<CountingSessionView>> getCountingSessionsByStatus(String status);

    /**
     * Gets variance report for a session.
     */
    CompletionStage<VarianceReportView> getVarianceReport(CountingSessionId sessionId);

    /**
     * Gets items needing verification for a session.
     */
    CompletionStage<List<CountingItemView>> getItemsNeedingVerification(CountingSessionId sessionId);

    /**
     * Gets items with variance for a session.
     */
    CompletionStage<List<CountingItemView>> getItemsWithVariance(CountingSessionId sessionId);
}