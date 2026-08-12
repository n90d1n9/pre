package tech.kayys.erp.billing.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.billing.application.api.BillingService;
import tech.kayys.erp.billing.application.api.command.*;
import tech.kayys.erp.billing.application.api.query.*;
import tech.kayys.erp.billing.application.port.InvoicePort;
import tech.kayys.erp.billing.application.port.PaymentPort;
import tech.kayys.erp.billing.application.port.NotificationPort;
import tech.kayys.erp.billing.domain.model.BillingSchedule;
import tech.kayys.erp.billing.domain.repository.BillingScheduleRepository;
import tech.kayys.erp.billing.domain.valueobject.BillingCycleStatus;
import tech.kayys.erp.billing.domain.valueobject.BillingStatus;
import tech.kayys.erp.billing.domain.valueobject.DunningAction;
import tech.kayys.erp.billing.domain.valueobject.DunningLevel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Core billing processing engine.
 */
@Singleton
@UseCase("Billing processing engine")
public class BillingProcessor implements BillingService {

    private final BillingScheduleRepository billingScheduleRepository;
    private final InvoicePort invoicePort;
    private final PaymentPort paymentPort;
    private final NotificationPort notificationPort;

    @Inject
    public BillingProcessor(
            BillingScheduleRepository billingScheduleRepository,
            InvoicePort invoicePort,
            PaymentPort paymentPort,
            NotificationPort notificationPort) {
        this.billingScheduleRepository = billingScheduleRepository;
        this.invoicePort = invoicePort;
        this.paymentPort = paymentPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public CompletionStage<BillingScheduleId> createBillingSchedule(CreateBillingScheduleCommand command) {
        BillingSchedule schedule = BillingSchedule.create(
            command.billingScheduleId() != null ? 
                command.billingScheduleId() : BillingScheduleId.generate(),
            command.subscriptionId(),
            command.customerId(),
            command.frequency(),
            Money.of(command.amount(), command.currencyCode()),
            command.currencyCode(),
            command.startDate()
        );

        schedule.setCustomerEmail(command.customerEmail());
        schedule.setTotalCycles(command.totalCycles());
        schedule.setMaxFailedPayments(command.maxFailedPayments());
        schedule.setPaymentMethodToken(command.paymentMethodToken());
        schedule.setSendEmailNotifications(command.sendEmailNotifications());
        schedule.setSendSmsNotifications(command.sendSmsNotifications());
        schedule.setCreatedBy(command.createdBy());

        return billingScheduleRepository.save(schedule)
            .thenApply(BillingSchedule::getId);
    }

    @Override
    public CompletionStage<BillingScheduleId> activateBillingSchedule(ActivateBillingScheduleCommand command) {
        return billingScheduleRepository.findById(command.scheduleId())
            .thenCompose(scheduleOpt -> {
                if (scheduleOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Billing schedule not found: " + command.scheduleId())
                    );
                }

                BillingSchedule schedule = scheduleOpt.get();
                schedule.activate();
                return billingScheduleRepository.save(schedule)
                    .thenApply(BillingSchedule::getId);
            });
    }

    @Override
    public CompletionStage<BillingScheduleId> pauseBillingSchedule(PauseBillingScheduleCommand command) {
        return billingScheduleRepository.findById(command.scheduleId())
            .thenCompose(scheduleOpt -> {
                if (scheduleOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Billing schedule not found: " + command.scheduleId())
                    );
                }

                BillingSchedule schedule = scheduleOpt.get();
                schedule.pause();
                return billingScheduleRepository.save(schedule)
                    .thenApply(BillingSchedule::getId);
            });
    }

    @Override
    public CompletionStage<BillingScheduleId> cancelBillingSchedule(CancelBillingScheduleCommand command) {
        return billingScheduleRepository.findById(command.scheduleId())
            .thenCompose(scheduleOpt -> {
                if (scheduleOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Billing schedule not found: " + command.scheduleId())
                    );
                }

                BillingSchedule schedule = scheduleOpt.get();
                schedule.cancel(command.reason());
                return billingScheduleRepository.save(schedule)
                    .thenApply(BillingSchedule::getId);
            });
    }

    @Override
    public CompletionStage<BillingCycleResult> processBillingCycle(ProcessBillingCycleCommand command) {
        return billingScheduleRepository.findById(command.scheduleId())
            .thenCompose(scheduleOpt -> {
                if (scheduleOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Billing schedule not found: " + command.scheduleId())
                    );
                }

                BillingSchedule schedule = scheduleOpt.get();

                if (schedule.getStatus() != BillingStatus.ACTIVE) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Schedule is not active: " + schedule.getStatus())
                    );
                }

                // Generate invoice
                return invoicePort.generateInvoice(
                    schedule.getCustomerId(),
                    schedule.getAmount(),
                    schedule.getCurrencyCode(),
                    "Billing cycle " + (schedule.getCurrentCycle() + 1)
                ).thenCompose(invoiceId -> {
                    // Process billing cycle
                    BillingSchedule.BillingCycle cycle = schedule.processBillingCycle(
                        Instant.now(),
                        invoiceId
                    );

                    // Process payment
                    return paymentPort.processPayment(
                        schedule.getPaymentMethodToken(),
                        schedule.getAmount(),
                        schedule.getCurrencyCode()
                    ).thenCompose(paymentResult -> {
                        if (paymentResult.success()) {
                            schedule.markCycleSuccess(cycle.getCycleNumber(), paymentResult.transactionId());
                            return billingScheduleRepository.save(schedule)
                                .thenApply(v -> new BillingCycleResult(
                                    schedule.getId(),
                                    cycle.getCycleNumber(),
                                    true,
                                    cycle.getAmount(),
                                    invoiceId,
                                    paymentResult.transactionId(),
                                    null,
                                    Instant.now()
                                ));
                        } else {
                            schedule.markCycleFailed(cycle.getCycleNumber(), paymentResult.message());
                            return billingScheduleRepository.save(schedule)
                                .thenApply(v -> new BillingCycleResult(
                                    schedule.getId(),
                                    cycle.getCycleNumber(),
                                    false,
                                    cycle.getAmount(),
                                    invoiceId,
                                    null,
                                    paymentResult.message(),
                                    Instant.now()
                                ));
                        }
                    });
                });
            });
    }

    @Override
    public CompletionStage<BatchBillingResult> processDueBillings(BatchBillingCommand command) {
        return billingScheduleRepository.findDueSchedules()
            .thenCompose(schedules -> {
                if (schedules.isEmpty()) {
                    return CompletableFuture.completedFuture(
                        new BatchBillingResult(0, 0, 0, 0, Money.zero("USD"), "No due schedules")
                    );
                }

                List<CompletableFuture<BillingCycleResult>> futures = schedules.stream()
                    .map(schedule -> {
                        ProcessBillingCycleCommand cycleCommand = new ProcessBillingCycleCommand(
                            schedule.getId()
                        );
                        return processBillingCycle(cycleCommand)
                            .toCompletableFuture();
                    })
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        List<BillingCycleResult> results = futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());

                        long successful = results.stream().filter(BillingCycleResult::success).count();
                        long failed = results.stream().filter(r -> !r.success()).count();

                        Money totalAmount = results.stream()
                            .map(BillingCycleResult::amount)
                            .reduce(Money.zero("USD"), Money::add);

                        return new BatchBillingResult(
                            schedules.size(),
                            (int) successful,
                            (int) failed,
                            results.size(),
                            totalAmount,
                            "Batch processing completed"
                        );
                    });
            });
    }

    @Override
    public CompletionStage<BillingCycleResult> retryBillingCycle(RetryBillingCycleCommand command) {
        // Implementation for retrying failed billing cycles
        return CompletableFuture.completedFuture(
            new BillingCycleResult(
                command.scheduleId(),
                0,
                false,
                Money.zero("USD"),
                null,
                null,
                "Retry not implemented",
                Instant.now()
            )
        );
    }

    @Override
    public CompletionStage<DunningResult> processDunning(ProcessDunningCommand command) {
        return billingScheduleRepository.findSchedulesWithPaymentFailures()
            .thenCompose(schedules -> {
                // Process dunning for each schedule
                return CompletableFuture.completedFuture(
                    new DunningResult(0, 0, 0, "Dunning processed")
                );
            });
    }

    @Override
    public CompletionStage<Void> handleDunningAction(HandleDunningActionCommand command) {
        // Implementation for handling dunning actions
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<BillingScheduleView> getBillingSchedule(BillingScheduleId scheduleId) {
        return billingScheduleRepository.findById(scheduleId)
            .thenApply(scheduleOpt -> scheduleOpt
                .map(BillingScheduleView::fromDomain)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Billing schedule not found: " + scheduleId
                ))
            );
    }

    @Override
    public CompletionStage<BillingScheduleView> getBillingScheduleBySubscription(UUID subscriptionId) {
        return billingScheduleRepository.findBySubscriptionId(subscriptionId)
            .thenApply(scheduleOpt -> scheduleOpt
                .map(BillingScheduleView::fromDomain)
                .orElse(null)
            );
    }

    @Override
    public CompletionStage<BillingHistoryView> getBillingHistory(String customerId) {
        return billingScheduleRepository.findByCustomerId(customerId)
            .thenApply(schedules -> {
                List<BillingScheduleView> views = schedules.stream()
                    .map(BillingScheduleView::fromDomain)
                    .collect(Collectors.toList());
                return new BillingHistoryView(customerId, views);
            });
    }

    @Override
    public CompletionStage<UpcomingBillingsView> getUpcomingBillings(UpcomingBillingsQuery query) {
        return billingScheduleRepository.findUpcomingBilling(query.daysAhead())
            .thenApply(schedules -> {
                List<BillingScheduleView> views = schedules.stream()
                    .map(BillingScheduleView::fromDomain)
                    .collect(Collectors.toList());
                return new UpcomingBillingsView(views, query.daysAhead());
            });
    }

    @Override
    public CompletionStage<BillingStatistics> getBillingStatistics(BillingStatisticsQuery query) {
        return billingScheduleRepository.getStatistics(query.fromDate(), query.toDate())
            .thenApply(stats -> new BillingStatistics(
                query.fromDate(),
                query.toDate(),
                stats.totalActiveSchedules(),
                stats.totalDueAmount(),
                stats.totalCollectedAmount(),
                stats.totalFailedPayments(),
                stats.successRate(),
                stats.averageRevenue()
            ));
    }
}