package tech.kayys.syirkah.crm.domain.repository;

import tech.kayys.syirkah.crm.domain.referral.Referral;
import tech.kayys.syirkah.crm.domain.referral.ReferralId;
import tech.kayys.syirkah.foundation.domain.repository.Repository;

/**
 * Repository port for {@link Referral} aggregates.
 */
public interface ReferralRepository extends Repository<Referral, ReferralId> {
}