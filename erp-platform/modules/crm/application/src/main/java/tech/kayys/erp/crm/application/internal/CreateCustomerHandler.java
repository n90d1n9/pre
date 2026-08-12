package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.CreateCustomerCommand;
import tech.kayys.erp.crm.domain.identifier.CustomerId;
import tech.kayys.erp.crm.domain.model.Customer;
import tech.kayys.erp.crm.domain.repository.CustomerRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating customers.
 */
@UseCase("Create a new customer")
public class CreateCustomerHandler implements CommandHandler<CreateCustomerCommand, CustomerId> {

    private final CustomerRepository customerRepository;

    @Inject
    public CreateCustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CompletionStage<CustomerId> handle(CreateCustomerCommand command) {
        // Check if customer already exists by email
        return customerRepository.existsByEmail(command.email())
            .thenCompose(exists -> {
                if (exists) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Customer with email already exists: " + command.email())
                    );
                }

                // Create the customer
                Customer customer = Customer.create(
                    command.customerId(),
                    command.customerNumber(),
                    command.companyName(),
                    command.email(),
                    command.currencyCode()
                );

                // Set optional fields
                if (command.firstName() != null) {
                    customer.setFirstName(command.firstName());
                }
                if (command.lastName() != null) {
                    customer.setLastName(command.lastName());
                }
                if (command.phone() != null) {
                    customer.setPhone(command.phone());
                }
                if (command.address() != null) {
                    customer.setAddress(command.address());
                }
                if (command.city() != null) {
                    customer.setCity(command.city());
                }
                if (command.state() != null) {
                    customer.setState(command.state());
                }
                if (command.postalCode() != null) {
                    customer.setPostalCode(command.postalCode());
                }
                if (command.country() != null) {
                    customer.setCountry(command.country());
                }
                if (command.industry() != null) {
                    customer.setIndustry(command.industry());
                }
                if (command.website() != null) {
                    customer.setWebsite(command.website());
                }
                if (command.taxId() != null) {
                    customer.setTaxId(command.taxId());
                }
                if (command.paymentTerms() != null) {
                    customer.setPaymentTerms(command.paymentTerms());
                }
                if (command.creditLimit() != null) {
                    customer.setCreditLimit(command.creditLimit());
                }
                if (command.accountStatus() != null) {
                    customer.setAccountStatus(command.accountStatus());
                }
                if (command.notes() != null) {
                    customer.setNotes(command.notes());
                }

                // Add contacts
                if (command.contacts() != null) {
                    for (CreateCustomerCommand.CustomerContactCommand contactCmd : command.contacts()) {
                        Customer.CustomerContact contact = new Customer.CustomerContact(
                            java.util.UUID.randomUUID().toString(),
                            contactCmd.firstName(),
                            contactCmd.lastName(),
                            contactCmd.email(),
                            contactCmd.phone(),
                            contactCmd.jobTitle(),
                            contactCmd.department(),
                            contactCmd.primary(),
                            true
                        );
                        customer.addContact(contact);
                    }
                }

                // Add addresses
                if (command.addresses() != null) {
                    for (CreateCustomerCommand.CustomerAddressCommand addressCmd : command.addresses()) {
                        Customer.CustomerAddress address = new Customer.CustomerAddress(
                            java.util.UUID.randomUUID().toString(),
                            addressCmd.type(),
                            addressCmd.address(),
                            addressCmd.city(),
                            addressCmd.state(),
                            addressCmd.postalCode(),
                            addressCmd.country()
                        );
                        customer.addAddress(address);
                    }
                }

                // Save the customer
                return customerRepository.save(customer)
                    .thenApply(Customer::getId);
            });
    }
}