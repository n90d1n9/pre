package tech.kayys.syirkah.asset.interfaces.producer;

import tech.kayys.syirkah.asset.application.fixedasset.FixedAssetService;
import tech.kayys.syirkah.asset.infrastructure.persistence.memory.InMemoryFixedAssetStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class FixedAssetServiceProducer {
    @Produces
    @ApplicationScoped
    public FixedAssetService fixedAssetService(InMemoryFixedAssetStore store) {
        return new FixedAssetService(store);
    }
}
