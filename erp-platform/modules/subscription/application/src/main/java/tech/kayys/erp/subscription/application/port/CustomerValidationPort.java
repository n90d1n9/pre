package tech.kayys.erp.subscription.application.port;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for validating customers from CRM context.
 */
public interface CustomerValidationPort {

    /**
     * Validates that a customer exists and is active.
     */
    CompletionStage<Boolean> validateCustomer(UUID customerId);

    /**
     * Gets customer details if needed.
     */
    CompletionStage<CustomerInfo> getCustomerInfo(UUID customerId);

    record CustomerInfo(
        UUID customerId,
        String email,
        String name,
        boolean active,
        String customerType
    ) {}
}