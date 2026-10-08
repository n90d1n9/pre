package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirement;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirementId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ComplianceRequirementRepository extends Repository<ComplianceRequirement, ComplianceRequirementId> {
    CompletionStage<Optional<ComplianceRequirement>> findByTenantAndCode(TenantId tenantId, String code);
    CompletionStage<List<ComplianceRequirement>> findActiveByTenant(TenantId tenantId);
}
