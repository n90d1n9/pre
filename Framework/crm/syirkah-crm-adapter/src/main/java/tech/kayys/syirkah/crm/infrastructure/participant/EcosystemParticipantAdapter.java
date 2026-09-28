package tech.kayys.syirkah.crm.infrastructure.participant;

import tech.kayys.syirkah.crm.application.participant.ParticipantPort;
import tech.kayys.syirkah.ecosystem.application.port.ParticipantRepository;
import tech.kayys.syirkah.ecosystem.domain.identifier.ParticipantId;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class EcosystemParticipantAdapter implements ParticipantPort {

    private final ParticipantRepository participantRepository;

    public EcosystemParticipantAdapter(ParticipantRepository participantRepository) {
        this.participantRepository = Objects.requireNonNull(participantRepository);
    }

    @Override
    public CompletionStage<Boolean> exists(ParticipantId participantId) {
        Objects.requireNonNull(participantId, "participantId cannot be null");
        return participantRepository.findById(participantId)
                .map(java.util.Optional::isPresent)
                .subscribe()
                .asCompletionStage();
    }
}
