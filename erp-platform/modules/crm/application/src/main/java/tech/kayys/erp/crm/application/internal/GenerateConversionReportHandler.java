package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.GenerateConversionReportCommand;
import tech.kayys.erp.crm.application.port.LeadAnalyticsPort;
import tech.kayys.erp.crm.domain.identifier.ReportId;
import tech.kayys.erp.crm.domain.model.ConversionReport;
import tech.kayys.erp.crm.domain.repository.ConversionReportRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for generating conversion reports.
 */
@UseCase("Generate a conversion report")
public class GenerateConversionReportHandler 
        implements CommandHandler<GenerateConversionReportCommand, ReportId> {

    private final ConversionReportRepository reportRepository;
    private final LeadAnalyticsPort leadAnalyticsPort;

    @Inject
    public GenerateConversionReportHandler(
            ConversionReportRepository reportRepository,
            LeadAnalyticsPort leadAnalyticsPort) {
        this.reportRepository = reportRepository;
        this.leadAnalyticsPort = leadAnalyticsPort;
    }

    @Override
    public CompletionStage<ReportId> handle(GenerateConversionReportCommand command) {
        // Get lead conversion data
        return leadAnalyticsPort.getConversionData(
                command.periodStart(),
                command.periodEnd()
            )
            .thenCompose(data -> {
                // Create report
                ConversionReport report = ConversionReport.create(
                    command.reportId(),
                    "Conversion Report - " + command.period(),
                    command.period(),
                    command.periodStart(),
                    command.periodEnd(),
                    command.generatedBy()
                );

                // Set data
                report.setTotalLeads(data.totalLeads());
                report.setConvertedLeads(data.convertedLeads());
                report.setLostLeads(data.lostLeads());
                report.setQualifiedLeads(data.qualifiedLeads());
                report.calculateConversionRate();

                // Add by source breakdown
                for (LeadAnalyticsPort.SourceData source : data.bySource()) {
                    report.addBySource(
                        new ConversionReport.ConversionSource(
                            source.source(),
                            source.total(),
                            source.converted()
                        )
                    );
                }

                // Add by industry breakdown
                for (LeadAnalyticsPort.IndustryData industry : data.byIndustry()) {
                    report.addByIndustry(
                        new ConversionReport.ConversionIndustry(
                            industry.industry(),
                            industry.total(),
                            industry.converted()
                        )
                    );
                }

                // Save report
                return reportRepository.save(report)
                    .thenApply(ConversionReport::getId);
            });
    }
}