package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.ConvertLeadCommand;
import tech.kayys.erp.crm.application.port.CustomerCreationPort;
import tech.kayys.erp.crm.domain.identifier.CustomerId;
import tech.kayys.erp.crm.domain.identifier.LeadId;
import tech.kayys.erp.crm.domain.model.Customer;
import tech.kayys.erp.crm.domain.model.Lead;
import tech.kayys.erp.crm.domain.repository.CustomerRepository;
import tech.kayys.erp.crm.domain.repository.LeadRepository;
import tech.kayys.erp.crm.domain.valueobject.LeadStatus;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for converting leads to customers.
 */
@UseCase("Convert a lead to a customer")
public class ConvertLeadHandler implements CommandHandler<ConvertLeadCommand, CustomerId> {

    private final LeadRepository leadRepository;
    private final CustomerRepository customerRepository;
    private final CustomerCreationPort customerCreationPort;

    @Inject
    public ConvertLeadHandler(
            LeadRepository leadRepository,
            CustomerRepository customerRepository,
            CustomerCreationPort customerCreationPort) {
        this.leadRepository = leadRepository;
        this.customerRepository = customerRepository;
        this.customerCreationPort = customerCreationPort;
    }

    @Override
    public CompletionStage<CustomerId> handle(ConvertLeadCommand command) {
        // 1. Find the lead
        return leadRepository.findById(command.leadId())
            .thenCompose(leadOpt -> {
                if (leadOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Lead not found: " + command.leadId())
                    );
                }

                Lead lead = leadOpt.get();

                // 2. Validate lead can be converted
                if (lead.getStatus() == LeadStatus.CONVERTED) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Lead already converted")
                    );
                }

                if (lead.getStatus() == LeadStatus.LOST || lead.getStatus() == LeadStatus.ARCHIVED) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Cannot convert " + lead.getStatus() + " lead")
                    );
                }

                // 3. Create customer
                Customer customer = customerCreationPort.createCustomerFromLead(lead, command);
                
                // 4. Save customer
                return customerRepository.save(customer)
                    .thenCompose(savedCustomer -> {
                        // 5. Update lead status
                        lead.changeStatus(LeadStatus.CONVERTED);
                        return leadRepository.save(lead)
                            .thenApply(v -> savedCustomer.getId());
                    });
            });
    }
}