package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.domain.model.VendorContract;
import tech.kayys.erp.purchasing.domain.repository.VendorContractRepository;
import tech.kayys.erp.purchasing.domain.valueobject.ContractStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Background processor for contract auto-renewals.
 */
@Singleton
@UseCase("Process contract auto-renewals")
public class ProcessAutoRenewalsHandler {

    private final VendorContractRepository contractRepository;

    @Inject
    public ProcessAutoRenewalsHandler(VendorContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    /**
     * Processes auto-renewals for all eligible contracts.
     * Returns the number of successfully renewed contracts.
     */
    public CompletionStage<Integer> processAutoRenewals() {
        return contractRepository.findContractsNeedingRenewal()
            .thenCompose(contracts -> {
                if (contracts.isEmpty()) {
                    return CompletableFuture.completedFuture(0);
                }

                // Process renewals in parallel
                List<CompletableFuture<VendorContract>> renewalFutures = contracts.stream()
                    .filter(contract -> contract.isAutoRenew() && contract.isActive())
                    .map(contract -> {
                        return processRenewal(contract)
                            .toCompletableFuture();
                    })
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(renewalFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        long count = renewalFutures.stream()
                            .filter(f -> !f.isCompletedExceptionally())
                            .count();
                        return (int) count;
                    });
            });
    }

    private CompletionStage<VendorContract> processRenewal(VendorContract contract) {
        // Calculate new expiration date
        Instant currentExpiration = contract.getExpirationDate();
        long durationDays = java.time.temporal.ChronoUnit.DAYS.between(
            contract.getEffectiveDate(),
            currentExpiration
        );
        
        Instant newExpiration = currentExpiration.plusSeconds(durationDays * 24L * 60L * 60L);

        // Renew the contract
        contract.renew(newExpiration);
        
        // Mark for renewal tracking
        contract.setNotes("Auto-renewed on " + Instant.now());
        contract.setLastModifiedBy("System");
        
        return contractRepository.save(contract);
    }
}