package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.compensation.CompensationTerms;
import tech.kayys.syirkah.workforce.domain.compensation.CompensationTermsId;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CompensationTermsRepository extends Repository<CompensationTerms, CompensationTermsId> {

    CompletionStage<List<CompensationTerms>> findByEmployment(EmploymentId employmentId);

    CompletionStage<Optional<CompensationTerms>> findEffective(EmploymentId employmentId, LocalDate date);
}
