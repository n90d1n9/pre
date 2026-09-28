package tech.kayys.syirkah.leave.spi;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.leave.domain.LeaveRequest;
import tech.kayys.syirkah.leave.domain.LeaveRequestId;

public interface LeaveRequestRepository extends Repository<LeaveRequest, LeaveRequestId> {}
