package tech.kayys.syirkah.crm.infrastructure.persistence.mapper;

import tech.kayys.syirkah.crm.domain.account.Account;
import tech.kayys.syirkah.crm.domain.identifier.AccountId;
import tech.kayys.syirkah.crm.domain.valueobject.AccountStatus;
import tech.kayys.syirkah.crm.domain.valueobject.PartyRole;
import tech.kayys.syirkah.ecosystem.domain.identifier.ParticipantId;
import tech.kayys.syirkah.crm.infrastructure.persistence.entity.AccountEntity;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<PartyRole> roles = account.partyRoles();
        entity.roles = roles.isEmpty() ? null : 
                roles.stream().map(PartyRole::name).collect(Collectors.joining(","));
        return entity;
    }

    public Account toDomain(AccountEntity entity) {
        Set<PartyRole> roleSet = (entity.roles == null || entity.roles.isBlank())
                ? Collections.emptySet()
                : Arrays.stream(entity.roles.split(","))
                        .map(String::trim)
                        .map(PartyRole::valueOf)
                        .collect(Collectors.toSet());
        return Account.restore(
                AccountId.of(entity.id),
                ParticipantId.of(entity.participantId),
                entity.name,
                entity.status,
                entity.parentAccountId == null ? null : AccountId.of(entity.parentAccountId),
                roleSet);
    }
}
