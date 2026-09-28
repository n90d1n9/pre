package tech.kayys.syirkah.crm.application.account;

import tech.kayys.syirkah.ecosystem.domain.identifier.ParticipantId;

public record CreateAccountCommand(ParticipantId participantId, String name) {
}
