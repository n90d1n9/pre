package tech.kayys.syirkah.budget.spi.port;

import tech.kayys.syirkah.budget.domain.*;

import java.math.BigDecimal;

public interface ActualRepository {
    void add(BudgetId budgetId, String accountCode, BudgetPeriod period, BigDecimal amount);
    BigDecimal totalFor(BudgetId budgetId, String accountCode, BudgetPeriod period);
}
