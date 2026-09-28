package tech.kayys.syirkah.crm.application.api.query;

import tech.kayys.syirkah.crm.domain.identifier.CustomerId;
import tech.kayys.syirkah.foundation.application.query.Query;

public record GetCustomerQuery(CustomerId customerId) implements Query {}
