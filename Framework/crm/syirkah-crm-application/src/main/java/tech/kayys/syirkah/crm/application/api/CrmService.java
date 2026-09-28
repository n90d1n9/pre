package tech.kayys.syirkah.crm.application.api;

import tech.kayys.syirkah.crm.application.api.command.*;
import tech.kayys.syirkah.crm.application.api.query.*;
import tech.kayys.syirkah.crm.domain.identifier.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface CrmService {

    // Leads
    CompletionStage<LeadId> createLead(CreateLeadCommand command);
    CompletionStage<LeadView> getLead(GetLeadQuery query);
    CompletionStage<CustomerId> convertLead(ConvertLeadCommand command);
    CompletionStage<List<LeadView>> searchLeads(SearchLeadsQuery query);

    // Customers
    CompletionStage<CustomerId> createCustomer(CreateCustomerCommand command);
    CompletionStage<CustomerView> getCustomer(GetCustomerQuery query);
    CompletionStage<Void> updateCustomer(UpdateCustomerCommand command);
    CompletionStage<Void> addCustomerContact(AddCustomerContactCommand command);
    CompletionStage<List<CustomerView>> searchCustomers(SearchCustomersQuery query);

    // Opportunities
    CompletionStage<OpportunityId> createOpportunity(CreateOpportunityCommand command);
    CompletionStage<OpportunityView> getOpportunity(GetOpportunityQuery query);
    CompletionStage<Void> updateOpportunity(UpdateOpportunityCommand command);
    CompletionStage<Void> moveOpportunityStage(MoveOpportunityStageCommand command);
    CompletionStage<PipelineView> getPipeline(String assignedTo, UUID customerId);
    CompletionStage<List<OpportunityView>> searchOpportunities(SearchOpportunitiesQuery query);

    // Tickets
    CompletionStage<TicketId> createTicket(CreateTicketCommand command);
    CompletionStage<Object> getTicket(GetTicketQuery query);
    CompletionStage<Void> assignTicket(AssignTicketCommand command);
    CompletionStage<Void> resolveTicket(ResolveTicketCommand command);
    CompletionStage<Void> closeTicket(CloseTicketCommand command);

    // Reports / Dashboard
    CompletionStage<CrmDashboardView> getDashboardMetrics(String period);
    CompletionStage<ReportId> generateConversionReport(GenerateConversionReportCommand command);
    CompletionStage<ConversionReportView> getConversionReport(String reportId);
    CompletionStage<ConversionReportView> getLatestConversionReport();
    CompletionStage<Object> getPipelineAnalytics(String assignedTo, UUID customerId);
    CompletionStage<Object> getLeadSourceAnalytics(String period, Instant fromDate, Instant toDate);

    // Email
    CompletionStage<EmailMessageId> sendEmail(SendEmailCommand command);
    CompletionStage<EmailTemplateId> createEmailTemplate(CreateEmailTemplateCommand command);
    CompletionStage<CampaignId> createEmailCampaign(CreateEmailCampaignCommand command);
    CompletionStage<Void> startEmailCampaign(StartEmailCampaignCommand command);

    // Customer Portal
    CompletionStage<CustomerPortalUserId> registerPortalUser(RegisterPortalUserCommand command);
    CompletionStage<TicketId> createPortalTicket(CreatePortalTicketCommand command);
    CompletionStage<Object> getPortalTicket(UUID ticketId);
    CompletionStage<List<Object>> getPortalTickets(UUID customerId, int page, int size);
    CompletionStage<List<Object>> searchKnowledgeArticles(String query, String category, int page, int size);
    CompletionStage<Object> getKnowledgeArticle(UUID id);
    CompletionStage<Void> markArticleHelpful(UUID id);
}
