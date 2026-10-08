# Promotion Capability Registry — P5 Promotion Resolver

## Scope
Wired the promotion capability registry into the compiler/evaluator and
implemented the P5 Promotion Resolver: a narrow candidate-reduction contract
between the transaction context and the evaluation engine.

## Architecture (product04.md section 17)

```
EvaluationContext
  → PromotionResolver           (candidate version ids)
  → PromotionRepository.loadAll  (bulk load definitions)
  → PromotionEngine.evaluate     (P6 eligibility)
```

The resolver returns versioned `PromotionCandidate` ids, NOT full aggregates.
Bulk loading is a separate step behind the `PromotionRepository` port, so
discovery stays storage-agnostic (product04.md section 28).

## New files
- `syirkah-promotion-domain/src/main/java/.../domain/PromotionVersionId.java`
- `syirkah-promotion-domain/src/main/java/.../domain/PromotionCandidate.java`
- `syirkah-promotion-domain/src/main/java/.../domain/PromotionResolveRequest.java`
- `syirkah-promotion-domain/src/main/java/.../domain/PromotionResolveRequestFactory.java`
- `syirkah-promotion-domain/src/main/java/.../domain/PromotionCandidateRepository.java`
- `syirkah-promotion-domain/src/main/java/.../domain/DefaultPromotionResolver.java`
- `syirkah-promotion-domain/src/main/java/.../domain/PromotionScope.java`
- `syirkah-promotion-domain/src/main/java/.../domain/context/TenantId.java`
- `syirkah-promotion-domain/src/main/java/.../domain/context/BranchId.java`
- `syirkah-promotion-domain/src/main/java/.../domain/context/ProductId.java`
- `syirkah-promotion-domain/src/main/java/.../domain/context/SkuId.java`
- `syirkah-promotion-domain/src/main/java/.../domain/context/CategoryId.java`
- `syirkah-promotion-domain/src/test/java/.../domain/PromotionResolverTest.java`
- `syirkah-promotion-spi/src/main/java/.../spi/port/PromotionCandidatePort.java`
- `syirkah-promotion-spi/src/main/java/.../spi/port/PromotionRepository.java`
- `syirkah-promotion-adapter/src/main/java/.../adapter/memory/InMemoryPromotionRepository.java`
- `syirkah-promotion-adapter/src/test/java/.../adapter/memory/InMemoryPromotionRepositoryFindCandidatesTest.java`
- `syirkah-promotion-application/src/main/java/.../application/handler/ApplyPromotionsHandler.java`
- `syirkah-promotion-application/src/test/java/.../application/support/StubPromotionCandidates.java`

## Modified files
- `PromotionStatus.java` — added SCHEDULED / PAUSED / EXPIRED / ARCHIVED / INACTIVE
- `Promotion.java` — added tenantId + scope, lifecycle transitions, scope setters
- `PromotionEvaluationContext.java` — added tenantId() / branchId()
- `DefaultPromotionEvaluationContext.java` — 8-arg constructor with tenant + branch
- `PromotionLineSnapshot.java` — added categoryId
- `PromotionCompiler.java` — reject INACTIVE and ARCHIVED
- `PromotionResolver.java` — returns `List<PromotionCandidate>` (version ids)
- `DefaultPromotionResolver.java` — delegates to `PromotionCandidateRepository`
- `PromotionCandidateRepository.java` — returns version ids, not aggregates
- `PromotionRepository.java` — added `loadAll(List<PromotionVersionId>)`
- `PromotionCandidatePort.java` — `findCandidates` returns version ids
- `InMemoryPromotionRepository.java` — implements both ports

## Test results
- promotion-domain: 46 green
- promotion-application: 2 green
- promotion-adapter: 4 green
- product-domain: 41 green
- foundation: 19 green
- All modules BUILD SUCCESS
