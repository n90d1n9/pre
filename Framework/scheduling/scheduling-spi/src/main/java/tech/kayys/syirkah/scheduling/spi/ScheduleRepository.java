package tech.kayys.syirkah.scheduling.spi;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.scheduling.domain.Schedule;
import tech.kayys.syirkah.scheduling.domain.ScheduleId;

public interface ScheduleRepository extends Repository<Schedule, ScheduleId> {}
