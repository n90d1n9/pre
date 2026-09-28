package tech.kayys.syirkah.crm.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import tech.kayys.syirkah.crm.domain.account.Account;
import tech.kayys.syirkah.crm.domain.identifier.AccountId;
import tech.kayys.syirkah.crm.domain.repository.AccountRepository;
import tech.kayys.syirkah.crm.infrastructure.persistence.entity.AccountEntity;
import tech.kayys.syirkah.crm.infrastructure.persistence.mapper.AccountMapper;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountMapper mapper;

    public AccountRepositoryImpl(AccountMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @WithTransaction
    public CompletionStage<Account> save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        return Panache.withTransaction(() -> entity.<AccountEntity>persist()
                .map(ignored -> account))
                .subscribe().asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Optional<Account>> findById(AccountId id) {
        return AccountEntity.<AccountEntity>findById(id.getValue())
                .map(entity -> entity == null
                        ? Optional.empty() : Optional.of(mapper.toDomain(entity)))
                .subscribe().asCompletionStage();
    }

    @Override
    @WithSession
    public CompletionStage<Boolean> existsById(AccountId id) {
        return AccountEntity.findById(id.getValue())
                .map(entity -> entity != null)
                .subscribe().asCompletionStage();
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> delete(Account account) {
        return deleteById(account.id());
    }

    @Override
    @WithTransaction
    public CompletionStage<Void> deleteById(AccountId id) {
        return AccountEntity.deleteById(id.getValue())
                .map(ignored -> (Void) null)
                .subscribe().asCompletionStage();
    }
}
