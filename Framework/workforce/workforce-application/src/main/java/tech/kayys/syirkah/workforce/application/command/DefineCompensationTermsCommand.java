package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.foundation.domain.valueobject.Money;
import tech.kayys.syirkah.workforce.domain.compensation.PayFrequency;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;

import java.time.LocalDate;
import java.util.Objects;

public record DefineCompensationTermsCommand(
        EmploymentId employmentId,
        Money basePay,
        PayFrequency payFrequency,
        LocalDate effectiveFrom
) implements Command {
    public DefineCompensationTermsCommand {
        Objects.requireNonNull(employmentId, "employmentId must not be null");
        Objects.requireNonNull(basePay, "basePay must not be null");
        Objects.requireNonNull(payFrequency, "payFrequency must not be null");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom must not be null");
    }
}
