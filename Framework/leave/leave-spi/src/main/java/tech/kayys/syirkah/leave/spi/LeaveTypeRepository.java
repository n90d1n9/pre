package tech.kayys.syirkah.leave.spi;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.leave.domain.LeaveType;
import tech.kayys.syirkah.leave.domain.LeaveTypeId;

public interface LeaveTypeRepository extends Repository<LeaveType, LeaveTypeId> {}
