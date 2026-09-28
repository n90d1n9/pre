package tech.kayys.syirkah.scheduling.spi;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.scheduling.domain.Shift;
import tech.kayys.syirkah.scheduling.domain.ShiftId;

public interface ShiftRepository extends Repository<Shift, ShiftId> {}
