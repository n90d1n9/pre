package tech.kayys.syirkah.asset.application.meter;
import java.math.BigDecimal;
import java.util.UUID;

public record MeterUsageResult(UUID meterId, String unit, BigDecimal startValue, BigDecimal endValue, BigDecimal usage, int readingCount) {}
