package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.AddCustomerContactCommand;
import tech.kayys.erp.crm.domain.identifier.CustomerId;
import tech.kayys.erp.crm.domain.model.Customer;
import tech.kayys.erp.crm.domain.repository.CustomerRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for adding a contact to a customer.
 */
@UseCase("Add a contact to a customer")
public class AddCustomerContactHandler implements CommandHandler<AddCustomerContactCommand, CustomerId> {

    private final CustomerRepository customerRepository;

    @Inject
    public AddCustomerContactHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CompletionStage<CustomerId> handle(AddCustomerContactCommand command) {
        return customerRepository.findById(command.customerId())
            .thenCompose(customerOpt -> {
                if (customerOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Customer not found: " + command.customerId())
                    );
                }

                Customer customer = customerOpt.get();

                // Check if contact already exists
                boolean contactExists = customer.getContacts().stream()
                    .anyMatch(c -> c.getEmail().equalsIgnoreCase(command.email()));
                if (contactExists) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Contact with email already exists: " + command.email())
                    );
                }

                // Create and add contact
                Customer.CustomerContact contact = new Customer.CustomerContact(
                    java.util.UUID.randomUUID().toString(),
                    command.firstName(),
                    command.lastName(),
                    command.email(),
                    command.phone(),
                    command.jobTitle(),
                    command.department(),
                    command.primary(),
                    true
                );

                customer.addContact(contact);

                // Save the customer
                return customerRepository.save(customer)
                    .thenApply(Customer::getId);
            });
    }
}