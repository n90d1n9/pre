package tech.kayys.syirkah.crm.infrastructure.bootstrap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import tech.kayys.syirkah.crm.application.api.CrmService;
import tech.kayys.syirkah.crm.application.api.CrmServiceImpl;
import tech.kayys.syirkah.crm.application.customer.AddCustomerContactHandler;
import tech.kayys.syirkah.crm.application.customer.CreateCustomerHandler;
import tech.kayys.syirkah.crm.application.customer.GetCustomerQueryHandler;
import tech.kayys.syirkah.crm.application.customer.SearchCustomersQueryHandler;
import tech.kayys.syirkah.crm.application.customer.UpdateCustomerHandler;
import tech.kayys.syirkah.crm.application.lead.ConvertLeadHandler;
import tech.kayys.syirkah.crm.application.lead.CreateLeadHandler;
import tech.kayys.syirkah.crm.application.lead.GetLeadQueryHandler;
import tech.kayys.syirkah.crm.application.lead.SearchLeadsQueryHandler;
import tech.kayys.syirkah.crm.application.opportunity.CreateOpportunityHandler;
import tech.kayys.syirkah.crm.application.opportunity.GetOpportunityQueryHandler;
import tech.kayys.syirkah.crm.application.opportunity.GetPipelineQueryHandler;
import tech.kayys.syirkah.crm.application.opportunity.MoveOpportunityStageHandler;
import tech.kayys.syirkah.crm.application.opportunity.SearchOpportunitiesQueryHandler;
import tech.kayys.syirkah.crm.application.opportunity.UpdateOpportunityHandler;
import tech.kayys.syirkah.crm.application.referral.AcceptReferralHandler;
import tech.kayys.syirkah.crm.application.referral.CancelReferralHandler;
import tech.kayys.syirkah.crm.application.referral.ConvertReferralHandler;
import tech.kayys.syirkah.crm.application.referral.CreateReferralHandler;
import tech.kayys.syirkah.crm.application.referral.RejectReferralHandler;
import tech.kayys.syirkah.crm.application.territory.ActivateTerritoryHandler;
import tech.kayys.syirkah.crm.application.territory.CreateTerritoryHandler;
import tech.kayys.syirkah.crm.application.territory.RetireTerritoryHandler;
import tech.kayys.syirkah.crm.application.territory.SetTerritoryParentHandler;
import tech.kayys.syirkah.crm.application.territory.UpdateTerritoryHandler;
import tech.kayys.syirkah.crm.application.territoryassignment.CreateTerritoryAssignmentHandler;
import tech.kayys.syirkah.crm.application.territoryassignment.EndTerritoryAssignmentHandler;
import tech.kayys.syirkah.crm.domain.repository.CustomerRepository;
import tech.kayys.syirkah.crm.domain.repository.LeadRepository;
import tech.kayys.syirkah.crm.domain.repository.OpportunityRepository;
import tech.kayys.syirkah.crm.domain.repository.ReferralRepository;
import tech.kayys.syirkah.crm.domain.repository.TerritoryAssignmentRepository;
import tech.kayys.syirkah.crm.domain.repository.TerritoryRepository;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;

/**
 * Composition root for the CRM service. Handlers are plain application-layer
 * classes with constructor-injected ports (no CDI annotations on the
 * application layer, so it stays framework-free); this is the one place that
 * wires the ports implemented in this adapter into the use cases defined in
 * syirkah-crm-application, and exposes the resulting {@link CrmService} bean
 * that the REST resources inject.
 */
@ApplicationScoped
public class CrmHandlerProducers {

    @Inject
    LeadRepository leadRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    OpportunityRepository opportunityRepository;

    @Inject
    ReferralRepository referralRepository;

    @Inject
    TerritoryRepository territoryRepository;

    @Inject
    TerritoryAssignmentRepository territoryAssignmentRepository;

    @Inject
    EventPublisher eventPublisher;

    @Inject
    UnitOfWork unitOfWork;

    @Produces
    @ApplicationScoped
    public CrmService crmService() {
        return new CrmServiceImpl(
                new CreateLeadHandler(leadRepository),
                new GetLeadQueryHandler(leadRepository),
                new ConvertLeadHandler(leadRepository, customerRepository),
                new SearchLeadsQueryHandler(leadRepository),

                new CreateCustomerHandler(customerRepository),
                new GetCustomerQueryHandler(customerRepository),
                new UpdateCustomerHandler(customerRepository),
                new AddCustomerContactHandler(customerRepository),
                new SearchCustomersQueryHandler(customerRepository),

                new CreateOpportunityHandler(opportunityRepository),
                new GetOpportunityQueryHandler(opportunityRepository),
                new UpdateOpportunityHandler(opportunityRepository),
                new MoveOpportunityStageHandler(opportunityRepository),
                new SearchOpportunitiesQueryHandler(opportunityRepository),
                new GetPipelineQueryHandler(opportunityRepository),

                new CreateReferralHandler(referralRepository, eventPublisher, unitOfWork),
                new AcceptReferralHandler(referralRepository, eventPublisher, unitOfWork),
                new RejectReferralHandler(referralRepository, eventPublisher, unitOfWork),
                new ConvertReferralHandler(referralRepository, eventPublisher, unitOfWork),
                new CancelReferralHandler(referralRepository, eventPublisher, unitOfWork),

                new CreateTerritoryHandler(territoryRepository, eventPublisher, unitOfWork),
                new UpdateTerritoryHandler(territoryRepository, eventPublisher, unitOfWork),
                new ActivateTerritoryHandler(territoryRepository, eventPublisher, unitOfWork),
                new RetireTerritoryHandler(territoryRepository, eventPublisher, unitOfWork),
                new SetTerritoryParentHandler(territoryRepository, eventPublisher, unitOfWork),

                new CreateTerritoryAssignmentHandler(territoryAssignmentRepository, eventPublisher, unitOfWork),
                new EndTerritoryAssignmentHandler(territoryAssignmentRepository, eventPublisher, unitOfWork));
    }
}
