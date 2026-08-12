package tech.kayys.erp.subscription.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.subscription.application.port.InvoiceGenerationPort;
import tech.kayys.erp.subscription.application.port.PaymentProcessorPort;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.repository.SubscriptionRepository;
import tech.kayys.erp.subscription.domain.valueobject.Money;
import tech.kayys.erp.subscription.domain.valueobject.SubscriptionStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Background processor for subscription renewals.
 */
@Singleton
@UseCase("Process subscription renewals")
public class ProcessRenewalsHandler {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceGenerationPort invoiceGenerationPort;
    private final PaymentProcessorPort paymentProcessorPort;

    @Inject
    public ProcessRenewalsHandler(
            SubscriptionRepository subscriptionRepository,
            InvoiceGenerationPort invoiceGenerationPort,
            PaymentProcessorPort paymentProcessorPort) {
        this.subscriptionRepository = subscriptionRepository;
        this.invoiceGenerationPort = invoiceGenerationPort;
        this.paymentProcessorPort = paymentProcessorPort;
    }

    /**
     * Processes all subscriptions that need renewal.
     * Returns the number of successfully renewed subscriptions.
     */
    public CompletionStage<Integer> processRenewals() {
        return subscriptionRepository.findSubscriptionsNeedingRenewal()
            .thenCompose(subscriptions -> {
                if (subscriptions.isEmpty()) {
                    return CompletableFuture.completedFuture(0);
                }

                // Process renewals in parallel
                List<CompletableFuture<Subscription>> renewalFutures = subscriptions.stream()
                    .map(subscription -> {
                        if (!subscription.isAutoRenew()) {
                            // Cancel subscriptions that don't auto-renew
                            subscription.cancel("Auto-renewal disabled");
                            return subscriptionRepository.save(subscription)
                                .thenApply(v -> subscription)
                                .toCompletableFuture();
                        }

                        return processRenewal(subscription)
                            .toCompletableFuture();
                    })
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(renewalFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        long count = renewalFutures.stream()
                            .filter(CompletableFuture::isCompletedExceptionally)
                            .count();
                        return (int) (subscriptions.size() - count);
                    });
            });
    }

    private CompletionStage<Subscription> processRenewal(Subscription subscription) {
        // 1. Generate invoice
        Money amount = subscription.getMonthlyFee();
        
        return invoiceGenerationPort.generateInvoice(subscription, amount)
            .thenCompose(invoiceId -> {
                // 2. Process payment
                return paymentProcessorPort.processPayment(subscription, amount)
                    .thenCompose(paymentResult -> {
                        if (paymentResult.success()) {
                            // 3. Record successful payment
                            subscription.recordPayment(invoiceId, amount);
                            subscription.renew();
                        } else {
                            // 4. Record payment failure
                            subscription.recordPaymentFailure();
                            
                            // If too many failures, enter grace period
                            if (subscription.getBillingAttempts() >= 3) {
                                subscription.enterGracePeriod();
                            }
                        }
                        
                        // 5. Save the updated subscription
                        return subscriptionRepository.save(subscription);
                    });
            });
    }
}