package tech.kayys.erp.transaction.application.mapper;

import tech.kayys.erp.transaction.domain.model.Transaction;
import tech.kayys.erp.transaction.domain.valueobject.TransactionType;
import tech.kayys.erp.accounting.domain.model.JournalEntry;
import tech.kayys.erp.accounting.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps transaction events to accounting entries.
 * Defines the accounting rules for each transaction type.
 */
public final class AccountingMapper {

    /**
     * Maps a transaction to journal entries.
     */
    public static List<JournalEntry> mapTransactionToJournalEntries(Transaction transaction) {
        List<JournalEntry> entries = new ArrayList<>();

        switch (transaction.getType()) {
            case SALE -> entries.add(mapSale(transaction));
            case AUTHORIZATION -> entries.add(mapAuthorization(transaction));
            case CAPTURE -> entries.add(mapCapture(transaction));
            case REFUND -> entries.add(mapRefund(transaction));
            case CHARGEBACK -> entries.add(mapChargeback(transaction));
            case REVERSAL -> entries.add(mapReversal(transaction));
            default -> throw new IllegalArgumentException(
                "Unsupported transaction type: " + transaction.getType()
            );
        }

        return entries;
    }

    private static JournalEntry mapSale(Transaction transaction) {
        // Debit: Accounts Receivable
        // Credit: Sales Revenue
        // Credit: Sales Tax (if applicable)
        // Debit: Payment Processing Fee (if applicable)

        JournalEntry entry = JournalEntry.create(
            JournalEntryId.generate(),
            "Sale transaction: " + transaction.getTransactionReference(),
            transaction.getTransactionReference()
        );
        entry.setSource("TRANSACTION", transaction.getId().toString());

        Money amount = transaction.getTotalAmount();
        Money taxAmount = transaction.getTaxAmount() != null ? 
            transaction.getTaxAmount() : Money.zero(transaction.getCurrencyCode());
        Money netAmount = amount.subtract(taxAmount);

        // Debit: Accounts Receivable
        JournalEntry.JournalLine arLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("AR-001")),
            JournalEntry.JournalLine.LineType.DEBIT,
            amount,
            "Accounts receivable from sale"
        );
        entry.addLine(arLine);

        // Credit: Sales Revenue
        JournalEntry.JournalLine revenueLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("REV-001")),
            JournalEntry.JournalLine.LineType.CREDIT,
            netAmount,
            "Sales revenue"
        );
        entry.addLine(revenueLine);

        // Credit: Sales Tax
        if (!taxAmount.isZero()) {
            JournalEntry.JournalLine taxLine = new JournalEntry.JournalLine(
                AccountId.of(UUID.fromString("TAX-001")),
                JournalEntry.JournalLine.LineType.CREDIT,
                taxAmount,
                "Sales tax liability"
            );
            entry.addLine(taxLine);
        }

        // Debit: Payment Processing Fee
        if (transaction.getFeeAmount() != null && !transaction.getFeeAmount().isZero()) {
            JournalEntry.JournalLine feeLine = new JournalEntry.JournalLine(
                AccountId.of(UUID.fromString("EXP-FEES")),
                JournalEntry.JournalLine.LineType.DEBIT,
                transaction.getFeeAmount(),
                "Payment processing fee"
            );
            entry.addLine(feeLine);
        }

        entry.post("SYSTEM");
        return entry;
    }

    private static JournalEntry mapAuthorization(Transaction transaction) {
        // Authorization only - hold on funds
        // Debit: Authorizations (Contra-AR)
        // Credit: Sales Revenue

        JournalEntry entry = JournalEntry.create(
            JournalEntryId.generate(),
            "Authorization: " + transaction.getTransactionReference(),
            transaction.getTransactionReference()
        );
        entry.setSource("TRANSACTION", transaction.getId().toString());

        Money amount = transaction.getTotalAmount();

        // Debit: Authorizations
        JournalEntry.JournalLine authLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("AUTH-001")),
            JournalEntry.JournalLine.LineType.DEBIT,
            amount,
            "Authorization hold"
        );
        entry.addLine(authLine);

        // Credit: Revenue (deferred)
        JournalEntry.JournalLine revenueLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("REV-DEFERRED")),
            JournalEntry.JournalLine.LineType.CREDIT,
            amount,
            "Deferred revenue - authorization"
        );
        entry.addLine(revenueLine);

        entry.post("SYSTEM");
        return entry;
    }

    private static JournalEntry mapCapture(Transaction transaction) {
        // Capture - convert authorization to actual sale
        // Debit: Accounts Receivable
        // Credit: Authorizations (remove hold)
        // Credit: Revenue (recognize)

        JournalEntry entry = JournalEntry.create(
            JournalEntryId.generate(),
            "Capture: " + transaction.getTransactionReference(),
            transaction.getTransactionReference()
        );
        entry.setSource("TRANSACTION", transaction.getId().toString());

        Money amount = transaction.getTotalAmount();

        // Debit: Accounts Receivable
        JournalEntry.JournalLine arLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("AR-001")),
            JournalEntry.JournalLine.LineType.DEBIT,
            amount,
            "Accounts receivable from capture"
        );
        entry.addLine(arLine);

        // Credit: Authorizations (remove)
        JournalEntry.JournalLine authLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("AUTH-001")),
            JournalEntry.JournalLine.LineType.CREDIT,
            amount,
            "Authorization hold released"
        );
        entry.addLine(authLine);

        // Credit: Revenue (recognize)
        JournalEntry.JournalLine revenueLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("REV-001")),
            JournalEntry.JournalLine.LineType.CREDIT,
            amount,
            "Revenue recognized from capture"
        );
        entry.addLine(revenueLine);

        entry.post("SYSTEM");
        return entry;
    }

    private static JournalEntry mapRefund(Transaction transaction) {
        // Refund - reverse the sale
        // Debit: Sales Returns
        // Credit: Accounts Receivable

        JournalEntry entry = JournalEntry.create(
            JournalEntryId.generate(),
            "Refund: " + transaction.getTransactionReference(),
            transaction.getTransactionReference()
        );
        entry.setSource("TRANSACTION", transaction.getId().toString());

        Money amount = transaction.getTotalAmount();

        // Debit: Sales Returns
        JournalEntry.JournalLine returnLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("RET-001")),
            JournalEntry.JournalLine.LineType.DEBIT,
            amount,
            "Sales return - refund"
        );
        entry.addLine(returnLine);

        // Credit: Accounts Receivable
        JournalEntry.JournalLine arLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("AR-001")),
            JournalEntry.JournalLine.LineType.CREDIT,
            amount,
            "AR reduction - refund"
        );
        entry.addLine(arLine);

        entry.post("SYSTEM");
        return entry;
    }

    private static JournalEntry mapChargeback(Transaction transaction) {
        // Chargeback - dispute
        // Debit: Chargeback Expense
        // Credit: Accounts Receivable

        JournalEntry entry = JournalEntry.create(
            JournalEntryId.generate(),
            "Chargeback: " + transaction.getTransactionReference(),
            transaction.getTransactionReference()
        );
        entry.setSource("TRANSACTION", transaction.getId().toString());

        Money amount = transaction.getTotalAmount();

        // Debit: Chargeback Expense
        JournalEntry.JournalLine expenseLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("EXP-CHARGEBACK")),
            JournalEntry.JournalLine.LineType.DEBIT,
            amount,
            "Chargeback expense"
        );
        entry.addLine(expenseLine);

        // Credit: Accounts Receivable
        JournalEntry.JournalLine arLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("AR-001")),
            JournalEntry.JournalLine.LineType.CREDIT,
            amount,
            "AR reduction - chargeback"
        );
        entry.addLine(arLine);

        entry.post("SYSTEM");
        return entry;
    }

    private static JournalEntry mapReversal(Transaction transaction) {
        // Reversal - reverse authorization
        // Debit: Revenue
        // Credit: Authorizations

        JournalEntry entry = JournalEntry.create(
            JournalEntryId.generate(),
            "Reversal: " + transaction.getTransactionReference(),
            transaction.getTransactionReference()
        );
        entry.setSource("TRANSACTION", transaction.getId().toString());

        Money amount = transaction.getTotalAmount();

        // Debit: Revenue
        JournalEntry.JournalLine revenueLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("REV-001")),
            JournalEntry.JournalLine.LineType.DEBIT,
            amount,
            "Revenue reversal"
        );
        entry.addLine(revenueLine);

        // Credit: Authorizations
        JournalEntry.JournalLine authLine = new JournalEntry.JournalLine(
            AccountId.of(UUID.fromString("AUTH-001")),
            JournalEntry.JournalLine.LineType.CREDIT,
            amount,
            "Authorization released"
        );
        entry.addLine(authLine);

        entry.post("SYSTEM");
        return entry;
    }
}