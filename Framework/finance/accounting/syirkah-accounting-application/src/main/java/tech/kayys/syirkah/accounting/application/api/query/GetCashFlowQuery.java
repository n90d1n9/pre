package tech.kayys.syirkah.accounting.application.api.query;

import tech.kayys.syirkah.accounting.application.cqrs.Query;
import tech.kayys.syirkah.accounting.domain.ledger.LedgerId;
import tech.kayys.syirkah.accounting.domain.multitenancy.TenantId;
import tech.kayys.syirkah.accounting.domain.report.CashFlowStatement;
import tech.kayys.syirkah.accounting.domain.report.ReportPeriod;

import java.util.Objects;

public record GetCashFlowQuery(
        TenantId tenantId,
        LedgerId ledgerId,
        ReportPeriod period
) implements Query<CashFlowStatement> {
    public GetCashFlowQuery {
        Objects.requireNonNull(period, "period cannot be null");
    }
}
