# Kayys ERP Platform — Foundation

This is the **foundation** of the ERP microservice platform: the small,
framework-free layer that every bounded context (Identity, Catalog,
Order, Inventory, Payment, ...) and every product (POS, e-commerce,
marketplace, coffee shop, ...) will build on top of.

It deliberately does **not** contain business concepts (`Order`,
`Product`, `Customer`). Those diverge per bounded context and must
never live in a shared library — see "Why this stays small" below.

## Modules

```text
erp-platform/
├── foundation/
│   ├── domain/        erp-foundation-domain       (Phase 1)
│   └── application/    erp-foundation-application  (Phase 2 / CQRS)
```

### `erp-foundation-domain` — pure Java, zero runtime dependencies

```text
tech.kayys.erp.foundation.domain
├── identifier    DomainId<T>
├── entity        Entity<ID>, AggregateRoot<ID>, AbstractAggregateRoot<ID>
├── valueobject   ValueObject, Money, Currency, Quantity, Unit, Percentage, DateRange
├── event         DomainEvent
├── time          DomainClock
└── exception     DomainException, BusinessRuleViolation, InvalidStateException
```

No `@Entity`, `@Path`, `@Inject`, no Mutiny `Uni`, no JSON annotations.
An aggregate exposes plain Java methods (`order.confirm()`), not
`Uni<Order> confirm()` — reactive orchestration is an *application*
concern, not a domain one.

### `erp-foundation-application` — CQRS building blocks, reactive orchestration

```text
tech.kayys.erp.foundation.application
├── command       Command, CommandHandler<C, R>
├── query         Query, QueryHandler<Q, R>
├── result        Result<T>, ApplicationError, ApplicationErrorException
├── page          Page<T>, PageRequest
├── event         EventPublisher            (outbound port)
└── transaction   UnitOfWork                (transaction-boundary port)
```

This module is allowed to depend on Mutiny (`io.smallrye.reactive:mutiny`)
and on `erp-foundation-domain` — nothing else. `CommandHandler`/
`QueryHandler` are where `Uni` orchestration lives; the aggregates they
call stay synchronous.

## Architecture rules, enforced by tests, not just by discipline

Each module has an ArchUnit test (`DomainArchitectureTest`,
`ApplicationArchitectureTest`) that fails the build if:

- `domain` ever depends on Quarkus, Hibernate, JPA, JAX-RS, Kafka,
  Redis, Jackson, or Mutiny.
- `application` ever depends on Quarkus, Hibernate, JPA, JAX-RS, Kafka,
  Redis, or Jackson directly.
- classes land outside the known subpackages of `domain` (guards
  against it slowly turning into a junk-drawer `common` module).

Dependency direction is one-way:

```text
Infrastructure  →  Application  →  Domain
```

Never the reverse, never a skip.

## Why this stays small (on purpose)

- **No generic `Repository<T, ID>`.** Each bounded context defines its
  own aggregate-specific repository port (`OrderRepository`,
  `InventoryRepository`, ...) in *its own* `application/port` package —
  not here. A generic repository abstraction fights DDD/ISP/SRP more
  than it helps.
- **No `TenantId`, `UserId`, `OrganizationId`, `CorrelationId` here.**
  These aren't universal primitives — they belong to Identity,
  Organization, and Tenant contexts respectively, and multi-tenancy
  (single-tenant / multi-tenant / multi-branch / marketplace-seller /
  franchise) is a platform capability to design deliberately, not bolt
  onto `Entity`.
- **No `BaseEntity` with `createdAt`/`updatedAt`/`tenantId`/`deleted`.**
  Auditing and tenancy are infrastructure/application concerns that
  get composed in, not inherited by every domain object whether it
  needs them or not.
- **No `Result`/`Either` in the domain layer.** Domain methods throw
  `BusinessRuleViolation` / `InvalidStateException`; `Result<T>` is an
  *application*-layer concept for command/query outcomes.
- **`Money` doesn't assume scale = 2.** Rounding/tax policy is a
  separate concern (`RoundingPolicy`, `TaxCalculationPolicy`) to be
  designed per accounting context, not baked into the primitive.

## What's intentionally not here yet

These are real, needed things — just not foundation-level yet:

- Bounded-context modules (`identity`, `catalog`, `order`, `inventory`, ...)
  and their own domain/application/adapter/bootstrap layers.
- The `erp-quarkus-platform` starter (reactive config, JSON,
  correlation ID, tenant context, security/JWT, observability, Kafka/
  Postgres/Redis conventions, transactional outbox, idempotency store,
  API error contract).
- Read-model / projection infrastructure for the CQRS query side.

## Running the tests

```bash
mvn -q test
```

Requires network access to Maven Central to resolve `junit-jupiter`,
`archunit-junit5`, and `io.smallrye.reactive:mutiny`.

## Next step

Pick the first bounded context (Identity is usually a good start —
almost everything depends on it) and build:

```text
identity/
├── identity-domain        depends on erp-foundation-domain only
├── identity-application    depends on erp-foundation-application
└── identity-adapter        depends on Quarkus/Postgres/Kafka + identity-application
```

then repeat the pattern per bounded context, composing capabilities
into products (POS, e-commerce, marketplace, ...) rather than sharing
their domain classes.
