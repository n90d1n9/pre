package tech.kayys.syirkah.crm.infrastructure.persistence.mapper;

import tech.kayys.syirkah.crm.domain.account.Account;
import tech.kayys.syirkah.crm.domain.identifier.AccountId;
import tech.kayys.syirkah.crm.domain.valueobject.AccountStatus;
import tech.kayys.syirkah.ecosystem.domain.identifier.ParticipantId;
import tech.kayys.syirkah.crm.infrastructure.persistence.entity.AccountEntity;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountMapper {

    public AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.id = account.id().getValue();
        entity.participantId = account.participantId().value();
        entity.parentAccountId = account.parentAccountId() == null
                ? null : account.parentAccountId().getValue();
        entity.name = account.name();
        entity.status = account.status();
        entity.active = account.status() == AccountStatus.ACTIVE;
        return entity;
    }

    public Account toDomain(AccountEntity entity) {
        return Account.restore(
                AccountId.of(entity.id),
                ParticipantId.of(entity.participantId),
                entity.name,
                entity.status,
                entity.parentAccountId == null ? null : AccountId.of(entity.parentAccountId));
    }
}
