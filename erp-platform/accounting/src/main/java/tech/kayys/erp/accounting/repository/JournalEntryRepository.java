package tech.kayys.erp.accounting.repository;


import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for JournalEntry aggregates.
 */
public interface JournalEntryRepository extends Repository<JournalEntry, JournalEntryId> {

    /**
     * Finds journal entries by source.
     */
    CompletionStage<List<JournalEntry>> findBySource(String sourceType, String sourceId);

    /**
     * Finds journal entries between dates.
     */
    CompletionStage<List<JournalEntry>> findByDateRange(Instant start, Instant end);

    /**
     * Finds posted journal entries.
     */
    CompletionStage<List<JournalEntry>> findPostedEntries();

    /**
     * Finds unposted journal entries.
     */
    CompletionStage<List<JournalEntry>> findUnpostedEntries();

    /**
     * Finds journal entries affecting a specific account.
     */
    CompletionStage<List<JournalEntry>> findByAccount(AccountId accountId);

    /**
     * Finds journal entries between dates for an account.
     */
    CompletionStage<List<JournalEntry>> findByAccountAndDateRange(
        AccountId accountId, Instant start, Instant end
    );
}