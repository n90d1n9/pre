package tech.kayys.syirkah.leave.spi;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.leave.domain.LeaveBalance;
import tech.kayys.syirkah.leave.domain.LeaveBalanceId;

public interface LeaveBalanceRepository extends Repository<LeaveBalance, LeaveBalanceId> {}
