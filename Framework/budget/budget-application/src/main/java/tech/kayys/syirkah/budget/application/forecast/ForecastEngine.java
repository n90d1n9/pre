package tech.kayys.syirkah.budget.application.forecast;

import tech.kayys.syirkah.budget.domain.BudgetPeriod;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ForecastEngine {
    Map<BudgetPeriod, BigDecimal> forecast(List<BudgetPeriod> historicalPeriods,
                                           List<BigDecimal> historicalTotals,
                                           List<BudgetPeriod> futurePeriods);
}
