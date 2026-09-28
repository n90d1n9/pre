# Product Capability

Product is a **reusable foundation**: a generic commercial/business
offering model that Retail, POS, Ecommerce, Procurement,
Manufacturing, Service, Accounting and ERP consume — not an
"ecommerce product module"
(`Docs/Product/product00.md`, `Docs/Product/product01.md`).

## Implemented modules (Product 1.0)

```text
product/
├── product-domain        Product, Variant, Specification, SKU + value objects + events
├── product-spi           persistence ports
├── product-application   use cases (commands + handlers)
├── product-adapter       in-memory adapters
├── product-core          (legacy, see below)
├── product-catalog       separate Catalog context
└── product-runtime       (legacy Quarkus prototype, see below)
```

### product-domain (pure Java, no frameworks)

```text
tech.kayys.syirkah.product.domain
├── product/         Product, ProductId, ProductType, ProductStatus
├── identifier/      ProductIdentifier, IdentifierType
├── variant/         ProductVariant, ProductVariantId, VariantStatus, VariantAttribute
├── specification/   ProductSpecification, AttributeDefinition, AttributeType,
│                    OptionGroup, OptionDefinition
├── sku/             Sku, SkuId, SkuStatus, SkuIdentifier, SkuIdentifierType
├── uom/             UnitOfMeasure, UnitCategory, UnitConversion
├── packaging/       Packaging
├── bundle/          BundleComponent
└── event/           18 product.* domain events
```

Four independent consistency boundaries — **Product**, **ProductVariant**,
**ProductSpecification**, **Sku** — referencing each other by ID only,
never by object graph (no God aggregate).

### Where the documented model was consolidated/improved

| Topic | Decision |
| --- | --- |
| Lifecycle | Strict, ordered `DRAFT → ACTIVE → DISCONTINUED → ARCHIVED`; every illegal transition throws. The docs' later idempotent sketch was rejected because the docs' own tests require `cannotActivateTwice` to fail. |
| Exceptions | `InvalidStateException` for state transitions, `BusinessRuleViolation` for duplicate identifiers/attributes and archived-immutability — matching Foundation's own distinction. |
| `ProductStatus` | 4 values (legacy `product-core` `INACTIVE` dropped; `ARCHIVED` is terminal). |
| `ProductType` | 6 values (`PHYSICAL, DIGITAL, SERVICE, SUBSCRIPTION, FEE, BUNDLE`) so nothing assumes physical goods. |
| `tenantId` | Deliberately absent from the domain — tenant context belongs to the adapter/platform layer (the docs' own later decision). |
| Identifiers | `ProductIdentifier(type, value, namespace)` on Product; `SkuIdentifier(type, value)` on SKU. Product-level codes and stockable-unit codes are different concerns. |
| Uniqueness | Product code and SKU code uniqueness enforced at the application layer via `existsByCode` ports (`PRODUCT_CODE_ALREADY_EXISTS`, `SKU_CODE_ALREADY_EXISTS`). |
| `UnitConversion` | Added — the docs require `1 BOX = 12 PCS` semantics but never modeled it; conversion is validated to stay inside one `UnitCategory`. |
| Options | `OptionDefinition` only (the docs' duplicate `Option` record was dropped). |
| Attribute changes | Typed `VariantAttribute` list with upsert by code (`changeAttribute`) and explicit `removeAttribute` instead of a raw `Map`. |

### Application layer (12 use cases)

`CreateProduct`, `UpdateProduct`, `ActivateProduct`, `DiscontinueProduct`,
`ArchiveProduct`, `AddProductIdentifier`, `CreateVariant`,
`CreateSpecification`, `AddSpecificationAttribute`,
`AddSpecificationOptionGroup`, `CreateSku`, `AddSkuIdentifier`.

Handlers orchestrate reactively (`Uni<Result<T>>`), publish the
aggregate's pending events through `EventPublisher`, and never touch
infrastructure directly.

### Enforced boundaries (ArchUnit)

* domain: no Quarkus/Hibernate/Kafka/Jackson, no Mutiny, no
  application/spi/adapter dependencies;
* domain: **no dependency on commerce, pricing, promotion, inventory,
  accounting, retail or ecommerce** (product00.md's core rule);
* application: no infrastructure, no adapter dependency.

## Deliberately out of scope (Product 1.0)

`price`, `promotion`, `discount`, `tax`, `inventory`, `warehouse`,
`customer`, `supplier`, `GL account`, subscription billing and order
management. Those are separate capabilities that consume Product IDs
and Product events (docs phases C–H: offering → pricing →
configuration → promotion → subscription/usage/entitlement → adapters).

Bundles are composition only — commercial bundle ≠ promotion ≠
manufacturing BOM.

## Legacy modules

* **`product-core`** — minimal earlier extraction from
  `Docs/Plan/arch02.md` (flat package, repository port inside the
  domain, weaker lifecycle, no events). Superseded by
  `product-domain` + `product-spi` + `product-application`. Retained
  on disk only because `product-runtime` still depends on its
  artifact; it is **not** part of the Product 1.0 API. Migration:
  move consumers to `syirkah-product-domain` and then remove it.
* **`product-catalog`** — a separate Catalog context
  (`tech.kayys.syirkah.catalog.*`), untouched by this work.
* **`product-runtime`** — pre-existing all-in-one Quarkus prototype
  (`com.saas.product.*`) whose duplicate model and REST/persistence
  concerns are still to be migrated or retired; its test suite was
  already failing before Product 1.0 was added.

