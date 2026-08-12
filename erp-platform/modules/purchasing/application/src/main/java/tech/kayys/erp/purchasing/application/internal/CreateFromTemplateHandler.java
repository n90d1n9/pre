package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.application.api.command.CreateFromTemplateCommand;
import tech.kayys.erp.purchasing.application.port.ContractTemplatePort;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.VendorContract;
import tech.kayys.erp.purchasing.domain.repository.VendorContractRepository;
import tech.kayys.erp.purchasing.domain.valueobject.ContractTemplate;
import tech.kayys.erp.purchasing.domain.valueobject.ContractType;
import tech.kayys.erp.purchasing.domain.valueobject.Money;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating contracts from templates.
 */
@UseCase("Create a contract from a template")
public class CreateFromTemplateHandler 
        implements CommandHandler<CreateFromTemplateCommand, ContractId> {

    private final VendorContractRepository contractRepository;
    private final ContractTemplatePort templatePort;

    @Inject
    public CreateFromTemplateHandler(
            VendorContractRepository contractRepository,
            ContractTemplatePort templatePort) {
        this.contractRepository = contractRepository;
        this.templatePort = templatePort;
    }

    @Override
    public CompletionStage<ContractId> handle(CreateFromTemplateCommand command) {
        // 1. Get the template
        return templatePort.getTemplate(command.templateId())
            .thenCompose(template -> {
                if (template == null || !template.isActive()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Template not found or inactive: " + command.templateId())
                    );
                }

                // 2. Create the contract from template
                VendorContract contract = VendorContract.create(
                    command.contractId(),
                    "CTR-" + System.currentTimeMillis(),
                    VendorId.of(command.vendorId()),
                    command.vendorName(),
                    template.getContractType(),
                    command.effectiveDate(),
                    command.expirationDate(),
                    command.currencyCode()
                );

                // 3. Set template details
                contract.setTitle(template.getName());
                contract.setDescription(template.getDescription());
                contract.setTemplateId(template.getId());

                // 4. Render template content with data
                if (command.data() != null) {
                    String renderedContent = template.renderWithData(command.data());
                    contract.setTermsAndConditions(renderedContent);
                }

                // 5. Set additional fields
                if (command.notes() != null) {
                    contract.setNotes(command.notes());
                }
                if (command.createdBy() != null) {
                    contract.setCreatedBy(command.createdBy());
                }

                // 6. Save the contract
                return contractRepository.save(contract)
                    .thenApply(VendorContract::getId);
            });
    }
}