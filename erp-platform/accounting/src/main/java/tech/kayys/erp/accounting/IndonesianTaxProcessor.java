
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

/**
 * Transaction processor with full Indonesian tax compliance.
 */
@ApplicationScoped
@UseCase("Process transactions with Indonesian tax")
public class IndonesianTaxProcessor {

    @Inject
    AccountRepository accountRepository;

    @Inject
    JournalEntryRepository journalEntryRepository;

    @Inject
    EFakturRepository eFakturRepository;

    @Inject
    WithholdingTaxRepository withholdingTaxRepository;

    /**
     * Processes a transaction with full Indonesian tax compliance.
     */
    public Uni<TransactionResult> processWithTax(Transaction transaction) {
        // 1. Calculate PPN (11% or 12%)
        PPNConfig ppnConfig = PPNConfig.getCurrentRate();
        Money baseAmount = transaction.getAmount();
        Money ppnAmount = baseAmount.multiply(ppnConfig.getRate());
        Money totalWithPPN = baseAmount.add(ppnAmount);

        // 2. Generate e-Faktur if B2B
        Uni<EFaktur> eFakturUni = Uni.createFrom().deferred(() -> {
            if (transaction.isB2B()) {
                EFaktur eFaktur = EFaktur.create(
                    EFakturId.generate(),
                    transaction.getId().toString(),
                    transaction.getOrderId().toString(),
                    transaction.getCustomerId(),
                    transaction.getCustomerName(),
                    transaction.getCustomerNPWP(),
                    transaction.getMerchantNPWP(),
                    transaction.getMerchantName(),
                    baseAmount,
                    ppnConfig,
                    transaction.getCurrencyCode()
                );
                eFaktur.generate();
                return Uni.createFrom().item(eFaktur);
            }
            return Uni.createFrom().nullItem();
        });

        // 3. Calculate PPh 23/26 if applicable
        Uni<WithholdingTax> withholdingUni = Uni.createFrom().deferred(() -> {
            if (transaction.isWithholdingApplicable()) {
                WithholdingTax.W withholdingType = transaction.isDomestic() ?
                    WithholdingTax.W.PPH_23 : WithholdingTax.W.PPH_26;
                
                WithholdingTax tax = WithholdingTax.create(
                    WithholdingTaxId.generate(),
                    transaction.getId().toString(),
                    transaction.getCustomerId(),
                    transaction.getCustomerNPWP(),
                    transaction.getCustomerName(),
                    withholdingType,
                    baseAmount,
                    transaction.getCurrencyCode(),
                    transaction.getTaxObjectType()
                );
                tax.calculateTax();
                return Uni.createFrom().item(tax);
            }
            return Uni.createFrom().nullItem();
        });

        // 4. Create accounting entries
        return Uni.combine()
            .all()
            .unis(eFakturUni, withholdingUni)
            .asTuple()
            .chain(tuple -> {
                EFaktur eFaktur = tuple.getItem1();
                WithholdingTax tax = tuple.getItem2();

                // Create journal entry
                JournalEntry entry = JournalEntry.create(
                    JournalEntryId.generate(),
                    "Transaction: " + transaction.getTransactionReference(),
                    transaction.getTransactionReference()
                );
                entry.setSource("TRANSACTION", transaction.getId().toString());

                // Debit: Accounts Receivable (Full amount including PPN)
                Money totalReceivable = transaction.getAmount().add(ppnAmount);
                if (tax != null) {
                    totalReceivable = totalReceivable.subtract(tax.getWithholdingAmount());
                }

                // Debit: AR
                JournalEntry.JournalLine arLine = new JournalEntry.JournalLine(
                    accountRepository.findByAccountNumber("AR-001").await().indefinitely().getId(),
                    JournalEntry.JournalLine.LineType.DEBIT,
                    totalReceivable,
                    "Transaction with PPN: " + transaction.getTransactionReference()
                );
                entry.addLine(arLine);

                // Credit: Revenue (Base amount)
                JournalEntry.JournalLine revenueLine = new JournalEntry.JournalLine(
                    accountRepository.findByAccountNumber("REV-001").await().indefinitely().getId(),
                    JournalEntry.JournalLine.LineType.CREDIT,
                    transaction.getAmount(),
                    "Revenue: " + transaction.getTransactionReference()
                );
                entry.addLine(revenueLine);

                // Credit: PPN Payable
                JournalEntry.JournalLine ppnLine = new JournalEntry.JournalLine(
                    accountRepository.findByAccountNumber("PPN-PAYABLE").await().indefinitely().getId(),
                    JournalEntry.JournalLine.LineType.CREDIT,
                    ppnAmount,
                    "PPN " + (ppnConfig.getRate().doubleValue() * 100) + "%"
                );
                entry.addLine(ppnLine);

                // Credit: Withholding Tax Payable (if applicable)
                if (tax != null) {
                    JournalEntry.JournalLine taxLine = new JournalEntry.JournalLine(
                        accountRepository.findByAccountNumber("TAX-PAYABLE").await().indefinitely().getId(),
                        JournalEntry.JournalLine.LineType.CREDIT,
                        tax.getWithholdingAmount(),
                        tax.getType().getDescription() + " - " + tax.getTaxCode()
                    );
                    entry.addLine(taxLine);
                }

                entry.post("SYSTEM");

                return journalEntryRepository.save(entry)
                    .chain(savedEntry -> {
                        // Save e-Faktur if generated
                        if (eFaktur != null) {
                            return eFakturRepository.save(eFaktur)
                                .chain(v -> Uni.createFrom().item(savedEntry));
                        }
                        return Uni.createFrom().item(savedEntry);
                    });
            });
    }
}