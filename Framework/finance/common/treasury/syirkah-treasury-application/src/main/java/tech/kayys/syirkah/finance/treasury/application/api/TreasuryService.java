package tech.kayys.syirkah.finance.treasury.application.api;

import tech.kayys.syirkah.finance.treasury.application.api.command.CloseDrawerCommand;
import tech.kayys.syirkah.finance.treasury.application.api.command.OpenDrawerCommand;
import tech.kayys.syirkah.finance.treasury.application.api.query.ZReportView;
import tech.kayys.syirkah.finance.treasury.domain.identifier.DrawerSessionId;

import java.util.concurrent.CompletionStage;

public interface TreasuryService {
    CompletionStage<DrawerSessionId> openSession(OpenDrawerCommand command);
    CompletionStage<ZReportView> closeSession(CloseDrawerCommand command);
    CompletionStage<ZReportView> getZReport(DrawerSessionId sessionId);
}
