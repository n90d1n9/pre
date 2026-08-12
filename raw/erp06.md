Yes. Let's make this **real code now**, using your package:

```text
tech.kayys.erp
```

I would slightly expand the original list because a reusable ERP foundation needs a few more primitives, but we should keep it deliberately small.

## Phase 1 target

```text
erp-foundation-domain
│
└── tech.kayys.erp.foundation.domain
    │
    ├── identifier
    │   └── DomainId
    │
    ├── entity
    │   ├── Entity
    │   ├── AggregateRoot
    │   └── AbstractAggregateRoot
    │
    ├── valueobject
    │   ├── ValueObject
    │   ├── Money
    │   ├── Currency
    │   ├── Quantity
    │   ├── Unit
    │   ├── Percentage
    │   └── DateRange
    │
    ├── event
    │   └── DomainEvent
    │
    └── exception
        ├── DomainException
        ├── BusinessRuleViolation
        └── InvalidStateException
```

And **nothing here depends on Quarkus**.

---

# 1. Project structure

```text
erp-platform/
├── pom.xml
│
├── foundation/
│   └── domain/
│       ├── pom.xml
│       │
│       └── src/
│           ├── main/
│           │   └── java/
│           │       └── tech/
│           │           └── kayys/
│           │               └── erp/
│           │                   └── foundation/
│           │                       └── domain/
│           │                           ├── identifier/
│           │                           ├── entity/
│           │                           ├── valueobject/
│           │                           ├── event/
│           │                           └── exception/
│           │
│           └── test/
│               └── java/
│                   └── tech/
│                       └── kayys/
│                           └── erp/
│                               └── foundation/
│                                   └── domain/
│
└── README.md
```

---

# 2. Root `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="
             http://maven.apache.org/POM/4.0.0
             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>tech.kayys.erp</groupId>
    <artifactId>erp-platform</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>Kayys ERP Platform</name>
    <description>
        Modular ERP platform based on DDD, SOLID and reactive microservices.
    </description>

    <modules>
        <module>foundation/domain</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.release>21</maven.compiler.release>

        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

        <junit.version>5.11.0</junit.version>
    </properties>

    <dependencyManagement>
        <dependencies>

            <dependency>
                <groupId>org.junit</groupId>
                <artifactId>junit-bom</artifactId>
                <version>${junit.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

        </dependencies>
    </dependencyManagement>

</project>
```

---

# 3. Foundation `pom.xml`

`foundation/domain/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="
             http://maven.apache.org/POM/4.0.0
             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-foundation-domain</artifactId>

    <name>ERP Foundation - Domain</name>

    <dependencies>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.0</version>
            </plugin>

        </plugins>
    </build>

</project>
```

Notice that JUnit is **test scope only**.

The production artifact has no dependencies.

---

# 4. `DomainId`

## `identifier/DomainId.java`

```java
package tech.kayys.erp.foundation.domain.identifier;

import java.util.UUID;

/**
 * Marker abstraction for strongly typed domain identifiers.
 *
 * @param <T> the identifier value type
 */
public interface DomainId<T> {

    T value();

}
```

I'm intentionally making this generic.

That gives us flexibility beyond UUID later.

For example:

```java
public record ProductId(UUID value)
        implements DomainId<UUID> {
}
```

Or potentially:

```java
public record LegacyProductId(String value)
        implements DomainId<String> {
}
```

---

# 5. Entity

## `entity/Entity.java`

```java
package tech.kayys.erp.foundation.domain.entity;

import tech.kayys.erp.foundation.domain.identifier.DomainId;

/**
 * An entity is defined by its identity rather than its attributes.
 *
 * @param <ID> strongly typed domain identifier
 */
public interface Entity<ID extends DomainId<?>> {

    ID id();

}
```

This gives us:

```java
public final class Product
        implements Entity<ProductId> {
}
```

rather than:

```java
Entity<UUID>
```

everywhere.

---

# 6. Value Object

## `valueobject/ValueObject.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

/**
 * Marker interface for immutable domain value objects.
 *
 * Value objects are defined by their attributes and have no identity.
 */
public interface ValueObject {
}
```

This is deliberately minimal.

A value object should generally be:

```text
immutable
self-validating
side-effect free
equality by value
```

Java records are perfect for many of them.

---

# 7. Aggregate Root

## `entity/AggregateRoot.java`

```java
package tech.kayys.erp.foundation.domain.entity;

import tech.kayys.erp.foundation.domain.event.DomainEvent;
import tech.kayys.erp.foundation.domain.identifier.DomainId;

import java.util.List;

/**
 * Root entity of a consistency boundary.
 *
 * @param <ID> aggregate identifier
 */
public interface AggregateRoot<ID extends DomainId<?>>
        extends Entity<ID> {

    /**
     * Pulls pending domain events from the aggregate.
     *
     * The returned events are removed from the aggregate.
     */
    List<DomainEvent> pullDomainEvents();

}
```

The important concept here is:

> An aggregate is not simply an entity with child entities.

It is a **transactional consistency boundary**.

We'll enforce that when we start building real domains.

---

# 8. Abstract Aggregate Root

## `entity/AbstractAggregateRoot.java`

```java
package tech.kayys.erp.foundation.domain.entity;

import tech.kayys.erp.foundation.domain.event.DomainEvent;
import tech.kayys.erp.foundation.domain.identifier.DomainId;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for aggregate roots.
 *
 * @param <ID> aggregate identifier
 */
public abstract class AbstractAggregateRoot<ID extends DomainId<?>>
        implements AggregateRoot<ID> {

    private final List<DomainEvent> domainEvents =
            new ArrayList<>();

    /**
     * Registers a domain event raised by this aggregate.
     */
    protected final void raise(DomainEvent event) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Domain event cannot be null"
            );
        }

        domainEvents.add(event);
    }

    /**
     * Returns all pending events and clears the internal collection.
     */
    @Override
    public final List<DomainEvent> pullDomainEvents() {
        if (domainEvents.isEmpty()) {
            return List.of();
        }

        final var events = List.copyOf(domainEvents);
        domainEvents.clear();

        return events;
    }

}
```

This gives us a very important pattern:

```text
Aggregate
   │
   ├── change state
   │
   └── raise event
          │
          ▼
    pending events
```

Later:

```text
Application
    │
    ▼
Repository.save(aggregate)
    │
    ▼
EventPublisher
```

---

# 9. Domain Event

## `event/DomainEvent.java`

```java
package tech.kayys.erp.foundation.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents something meaningful that happened in the domain.
 */
public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();

    String eventType();

}
```

A real event later might look like:

```java
public record ProductCreated(
        UUID eventId,
        Instant occurredAt,
        ProductId productId
) implements DomainEvent {

    @Override
    public String eventType() {
        return "catalog.product.created";
    }
}
```

Notice:

```text
DomainEvent
```

doesn't know anything about Kafka.

That's exactly what we want.

---

# 10. Domain Exception

## `exception/DomainException.java`

```java
package tech.kayys.erp.foundation.domain.exception;

/**
 * Base exception for domain-level failures.
 */
public class DomainException
        extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }

}
```

---

# 11. Business Rule Violation

## `exception/BusinessRuleViolation.java`

```java
package tech.kayys.erp.foundation.domain.exception;

/**
 * Raised when a domain business rule is violated.
 */
public class BusinessRuleViolation
        extends DomainException {

    public BusinessRuleViolation(String message) {
        super(message);
    }

}
```

Example later:

```java
throw new BusinessRuleViolation(
    "Cannot confirm an empty order"
);
```

---

# 12. Invalid State

## `exception/InvalidStateException.java`

```java
package tech.kayys.erp.foundation.domain.exception;

/**
 * Raised when an aggregate or domain object
 * is in a state that does not allow an operation.
 */
public class InvalidStateException
        extends DomainException {

    public InvalidStateException(String message) {
        super(message);
    }

}
```

The distinction is useful:

```text
BusinessRuleViolation
    "Customer cannot purchase more than credit limit"

InvalidStateException
    "Order is already cancelled"
```

We'll refine our error taxonomy later when we reach the application/API layer.

---

# 13. Currency

Now we start adding actual business primitives.

## `valueobject/Currency.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

/**
 * ISO-4217 style currency code.
 */
public record Currency(String code)
        implements ValueObject {

    public Currency {
        Objects.requireNonNull(
                code,
                "Currency code cannot be null"
        );

        code = code.trim().toUpperCase(Locale.ROOT);

        if (!code.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Currency code must contain exactly 3 letters"
            );
        }
    }

    public static Currency of(String code) {
        return new Currency(code);
    }

    public static Currency IDR() {
        return new Currency("IDR");
    }

    public static Currency USD() {
        return new Currency("USD");
    }

    public static Currency EUR() {
        return new Currency("EUR");
    }

}
```

Usage:

```java
var idr = Currency.IDR();
var usd = Currency.USD();
```

---

# 14. Money

## `valueobject/Money.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

/**
 * Monetary value with an explicit currency.
 *
 * Money is immutable.
 */
public record Money(
        BigDecimal amount,
        Currency currency
) implements ValueObject {

    public Money {
        Objects.requireNonNull(
                amount,
                "Money amount cannot be null"
        );

        Objects.requireNonNull(
                currency,
                "Money currency cannot be null"
        );
    }

    public static Money zero(Currency currency) {
        return new Money(ZERO, currency);
    }

    public static Money of(
            BigDecimal amount,
            Currency currency
    ) {
        return new Money(amount, currency);
    }

    public static Money of(
            long amount,
            Currency currency
    ) {
        return new Money(
                BigDecimal.valueOf(amount),
                currency
        );
    }

    public Money add(Money other) {
        requireSameCurrency(other);

        return new Money(
                amount.add(other.amount),
                currency
        );
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);

        return new Money(
                amount.subtract(other.amount),
                currency
        );
    }

    public Money multiply(BigDecimal multiplier) {
        Objects.requireNonNull(
                multiplier,
                "Multiplier cannot be null"
        );

        return new Money(
                amount.multiply(multiplier),
                currency
        );
    }

    public Money negate() {
        return new Money(
                amount.negate(),
                currency
        );
    }

    public Money abs() {
        return new Money(
                amount.abs(),
                currency
        );
    }

    public boolean isZero() {
        return amount.compareTo(ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(ZERO) < 0;
    }

    public int compareTo(Money other) {
        requireSameCurrency(other);

        return amount.compareTo(other.amount);
    }

    public boolean greaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean greaterThanOrEqual(Money other) {
        return compareTo(other) >= 0;
    }

    public boolean lessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean lessThanOrEqual(Money other) {
        return compareTo(other) <= 0;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(
                other,
                "Money cannot be null"
        );

        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Currency mismatch: "
                            + currency.code()
                            + " vs "
                            + other.currency.code()
            );
        }
    }

}
```

### Important

I deliberately **did not** force:

```text
scale = 2
```

into `Money`.

For ERP/accounting, we will eventually need a proper:

```text
RoundingPolicy
MoneyMath
CurrencyRules
TaxCalculationPolicy
```

rather than making `Money` incorrectly assume every currency/calculation uses two decimal places.

---

# 15. Unit

Now, because your platform will eventually handle:

* grocery
* warehouse
* coffee recipes
* manufacturing
* purchasing
* inventory

we need quantities.

But we should not put `Kilogram`, `Liter`, etc. directly into `Quantity`.

Create:

## `valueobject/Unit.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import java.util.Objects;

/**
 * Unit of measurement.
 *
 * Examples:
 * kg, g, l, ml, pcs, box
 */
public record Unit(String code)
        implements ValueObject {

    public Unit {
        Objects.requireNonNull(
                code,
                "Unit code cannot be null"
        );

        code = code.trim();

        if (code.isBlank()) {
            throw new IllegalArgumentException(
                    "Unit code cannot be blank"
            );
        }
    }

    public static Unit of(String code) {
        return new Unit(code);
    }

}
```

We intentionally don't define:

```java
enum Unit {
    KG,
    GRAM,
    LITER,
    ...
}
```

because the actual ERP unit catalog will eventually need:

```text
Unit
 ├── kg
 ├── g
 ├── mg
 ├── liter
 ├── ml
 ├── piece
 ├── box
 ├── carton
 └── ...
```

plus conversion rules.

That belongs in the Inventory/UOM domain later.

---

# 16. Quantity

## `valueobject/Quantity.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Measurable domain quantity.
 */
public record Quantity(
        BigDecimal value,
        Unit unit
) implements ValueObject {

    public Quantity {
        Objects.requireNonNull(
                value,
                "Quantity value cannot be null"
        );

        Objects.requireNonNull(
                unit,
                "Quantity unit cannot be null"
        );

        if (value.signum() < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative"
            );
        }
    }

    public static Quantity of(
            BigDecimal value,
            Unit unit
    ) {
        return new Quantity(value, unit);
    }

    public static Quantity of(
            long value,
            Unit unit
    ) {
        return new Quantity(
                BigDecimal.valueOf(value),
                unit
        );
    }

    public boolean isZero() {
        return value.signum() == 0;
    }

    public boolean isPositive() {
        return value.signum() > 0;
    }

    public Quantity add(Quantity other) {
        requireSameUnit(other);

        return new Quantity(
                value.add(other.value),
                unit
        );
    }

    public Quantity subtract(Quantity other) {
        requireSameUnit(other);

        var result = value.subtract(other.value);

        if (result.signum() < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot become negative"
            );
        }

        return new Quantity(result, unit);
    }

    public Quantity multiply(BigDecimal multiplier) {
        Objects.requireNonNull(
                multiplier,
                "Multiplier cannot be null"
        );

        var result = value.multiply(multiplier);

        if (result.signum() < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot become negative"
            );
        }

        return new Quantity(result, unit);
    }

    private void requireSameUnit(Quantity other) {
        Objects.requireNonNull(
                other,
                "Quantity cannot be null"
        );

        if (!unit.equals(other.unit)) {
            throw new IllegalArgumentException(
                    "Unit mismatch: "
                            + unit.code()
                            + " vs "
                            + other.unit.code()
            );
        }
    }

}
```

Later, we can change this to support:

```text
kg + g
liter + ml
```

through a `UnitConversionService`.

Don't put conversion logic into `Quantity`.

---

# 17. Percentage

Discounts, tax, commission, margin, marketplace fees, etc. will need percentages.

## `valueobject/Percentage.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Percentage represented as a human-readable percentage.
 *
 * 10% is represented by BigDecimal("10"),
 * not BigDecimal("0.10").
 */
public record Percentage(
        BigDecimal value
) implements ValueObject {

    public Percentage {
        Objects.requireNonNull(
                value,
                "Percentage cannot be null"
        );

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Percentage cannot be negative"
            );
        }

        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(
                    "Percentage cannot exceed 100"
            );
        }
    }

    public static Percentage zero() {
        return new Percentage(BigDecimal.ZERO);
    }

    public static Percentage of(BigDecimal value) {
        return new Percentage(value);
    }

    public static Percentage of(double value) {
        return new Percentage(
                BigDecimal.valueOf(value)
        );
    }

    /**
     * Converts 10% into 0.10.
     */
    public BigDecimal factor() {
        return value.divide(
                BigDecimal.valueOf(100)
        );
    }

}
```

Usage:

```java
var tenPercent = Percentage.of(10);

tenPercent.factor();
// 0.10
```

This is useful later for:

```text
Tax = 11%
Discount = 10%
Commission = 2.5%
```

---

# 18. DateRange

ERP systems constantly deal with:

```text
accounting periods
promotion periods
subscription periods
price validity
employee contracts
academic terms
project dates
```

So a generic `DateRange` is justified.

## `valueobject/DateRange.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Inclusive date range.
 */
public record DateRange(
        LocalDate start,
        LocalDate end
) implements ValueObject {

    public DateRange {
        Objects.requireNonNull(
                start,
                "Start date cannot be null"
        );

        Objects.requireNonNull(
                end,
                "End date cannot be null"
        );

        if (end.isBefore(start)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }

    public boolean contains(LocalDate date) {
        Objects.requireNonNull(date);

        return !date.isBefore(start)
                && !date.isAfter(end);
    }

    public boolean overlaps(DateRange other) {
        Objects.requireNonNull(other);

        return !end.isBefore(other.start)
                && !other.end.isBefore(start);
    }

    public boolean isSingleDay() {
        return start.equals(end);
    }

}
```

---

# 19. Domain clock

One more thing I strongly recommend at the foundation level is a **domain clock abstraction**.

Why?

Because later we don't want domain/application code doing:

```java
Instant.now()
```

everywhere.

That makes testing harder.

Create:

## `time/DomainClock.java`

```java
package tech.kayys.erp.foundation.domain.time;

import java.time.Instant;

public interface DomainClock {

    Instant now();

}
```

And a system implementation can come later:

```java
public final class SystemDomainClock
        implements DomainClock {

    @Override
    public Instant now() {
        return Instant.now();
    }

}
```

But **the system implementation doesn't belong in the pure domain artifact**.

For now, only the interface.

---

# 20. Domain event factory?

I would **not** create one.

Don't do this:

```java
DomainEventFactory.create(...)
```

Events should be explicit:

```java
new ProductCreated(...)
```

It keeps the domain readable.

---

# 21. Result / Either?

I would also **not add `Result<T>` yet**.

It's tempting to create:

```text
Result
Either
Option
ErrorOr
```

but that can become a framework-like abstraction before we know how our application layer wants to handle errors.

We'll design that in **Phase 2** when we introduce commands and use cases.

---

# 22. Domain service?

Also don't create:

```java
DomainService
```

as a marker interface.

A domain service should only exist when we have a genuine domain operation that doesn't naturally belong to an entity/value object.

For example later:

```text
PricingService
TaxCalculationService
UnitConversionService
```

But we shouldn't manufacture a generic base interface now.

---

# 23. Domain event metadata

I would keep the initial event:

```java
UUID eventId();
Instant occurredAt();
String eventType();
```

Later, when we introduce messaging, we'll likely add:

```text
tenantId
aggregateId
aggregateType
correlationId
causationId
version
```

But those are partly **integration/message metadata**, and I don't want to pollute the pure domain event abstraction prematurely.

---

# 24. Tests

Now let's make the foundation trustworthy.

## `CurrencyTest.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void normalizesCurrencyCode() {
        var currency = Currency.of(" idr ");

        assertEquals("IDR", currency.code());
    }

    @Test
    void rejectsInvalidCurrencyCode() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Currency.of("ID")
        );
    }

    @Test
    void supportsKnownCurrencies() {
        assertEquals(
                "IDR",
                Currency.IDR().code()
        );

        assertEquals(
                "USD",
                Currency.USD().code()
        );
    }

}
```

---

# 25. Money tests

## `MoneyTest.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    private static final Currency IDR =
            Currency.IDR();

    private static final Currency USD =
            Currency.USD();

    @Test
    void addsSameCurrency() {

        var first = Money.of(
                new BigDecimal("10000"),
                IDR
        );

        var second = Money.of(
                new BigDecimal("5000"),
                IDR
        );

        var result = first.add(second);

        assertEquals(
                new BigDecimal("15000"),
                result.amount()
        );

        assertEquals(IDR, result.currency());
    }

    @Test
    void subtractsSameCurrency() {

        var first = Money.of(
                new BigDecimal("10000"),
                IDR
        );

        var second = Money.of(
                new BigDecimal("3000"),
                IDR
        );

        var result = first.subtract(second);

        assertEquals(
                new BigDecimal("7000"),
                result.amount()
        );
    }

    @Test
    void multipliesMoney() {

        var money = Money.of(
                new BigDecimal("10000"),
                IDR
        );

        var result = money.multiply(
                new BigDecimal("2.5")
        );

        assertEquals(
                new BigDecimal("25000.0"),
                result.amount()
        );
    }

    @Test
    void rejectsCurrencyMismatch() {

        var idr = Money.of(
                new BigDecimal("10000"),
                IDR
        );

        var usd = Money.of(
                new BigDecimal("10"),
                USD
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> idr.add(usd)
        );
    }

    @Test
    void detectsPositiveMoney() {

        var money = Money.of(
                10000,
                IDR
        );

        assertTrue(money.isPositive());
        assertFalse(money.isZero());
        assertFalse(money.isNegative());
    }

    @Test
    void detectsNegativeMoney() {

        var money = Money.of(
                -10000,
                IDR
        );

        assertTrue(money.isNegative());
    }

    @Test
    void comparesMoney() {

        var first = Money.of(
                10000,
                IDR
        );

        var second = Money.of(
                5000,
                IDR
        );

        assertTrue(first.greaterThan(second));
        assertTrue(second.lessThan(first));
    }

}
```

---

# 26. Quantity tests

## `QuantityTest.java`

```java
package tech.kayys.erp.foundation.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    private static final Unit KG =
            Unit.of("kg");

    private static final Unit LITER =
            Unit.of("liter");

    @Test
    void addsSameUnit() {

        var first = Quantity.of(
                new BigDecimal("2.5"),
                KG
        );

        var second = Quantity.of(
                new BigDecimal("1.5"),
                KG
        );

        var result = first.add(second);

        assertEquals(
                new BigDecimal("4.0"),
                result.value()
        );
    }

    @Test
    void rejectsDifferentUnits() {

        var kg = Quantity.of(
                new BigDecimal("2"),
                KG
        );

        var liter = Quantity.of(
                new BigDecimal("2"),
                LITER
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> kg.add(liter)
        );
    }

    @Test
    void rejectsNegativeQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Quantity.of(
                        new BigDecimal("-1"),
                        KG
                )
        );
    }

}
```

---

# 27. Percentage tests

```java
package tech.kayys.erp.foundation.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PercentageTest {

    @Test
    void representsTenPercent() {

        var percentage =
                Percentage.of(10);

        assertEquals(
                new BigDecimal("10"),
                percentage.value()
        );

        assertEquals(
                new BigDecimal("0.1"),
                percentage.factor()
        );
    }

    @Test
    void rejectsMoreThanOneHundredPercent() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Percentage.of(101)
        );
    }

    @Test
    void rejectsNegativePercentage() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Percentage.of(-1)
        );
    }

}
```

---

# 28. Aggregate event test

Now let's prove the most important behavior.

Create a test ID:

```java
package tech.kayys.erp.foundation.domain.entity;

import tech.kayys.erp.foundation.domain.event.DomainEvent;
import tech.kayys.erp.foundation.domain.identifier.DomainId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AbstractAggregateRootTest {

    record TestId(UUID value)
            implements DomainId<UUID> {
    }

    record SomethingHappened(
            UUID eventId,
            Instant occurredAt
    ) implements DomainEvent {

        @Override
        public String eventType() {
            return "test.something-happened";
        }
    }

    static final class TestAggregate
            extends AbstractAggregateRoot<TestId> {

        private final TestId id;

        TestAggregate(TestId id) {
            this.id = id;
        }

        void doSomething() {
            raise(
                    new SomethingHappened(
                            UUID.randomUUID(),
                            Instant.now()
                    )
            );
        }

        @Override
        public TestId id() {
            return id;
        }
    }

    @Test
    void pullsAndClearsDomainEvents() {

        var aggregate =
                new TestAggregate(
                        new TestId(UUID.randomUUID())
                );

        aggregate.doSomething();

        var events =
                aggregate.pullDomainEvents();

        assertEquals(1, events.size());

        assertTrue(
                aggregate
                        .pullDomainEvents()
                        .isEmpty()
        );
    }

}
```

---

# 29. One change I'd make now: event timestamps

Our test currently uses:

```java
Instant.now()
```

That's okay for the test.

But in actual domain code we should eventually inject:

```java
DomainClock
```

and create:

```java
clock.now()
```

rather than:

```java
Instant.now()
```

This becomes particularly useful for:

```text
Subscription renewal
Accounting period
Promotion expiration
Inventory expiry
Education enrollment
Project deadlines
```

We'll introduce the proper pattern in Phase 2.

---

# 30. Final Phase 1 package

So our actual foundation now looks like:

```text
tech.kayys.erp.foundation.domain
│
├── identifier
│   └── DomainId.java
│
├── entity
│   ├── Entity.java
│   ├── AggregateRoot.java
│   └── AbstractAggregateRoot.java
│
├── valueobject
│   ├── ValueObject.java
│   ├── Currency.java
│   ├── Money.java
│   ├── Unit.java
│   ├── Quantity.java
│   ├── Percentage.java
│   └── DateRange.java
│
├── event
│   └── DomainEvent.java
│
├── time
│   └── DomainClock.java
│
└── exception
    ├── DomainException.java
    ├── BusinessRuleViolation.java
    └── InvalidStateException.java
```

That's a good **Phase 1 foundation**.

---

# 31. Why I deliberately stopped here

There are many things we *could* put here:

```text
Address
EmailAddress
PhoneNumber
TaxCode
SKU
Barcode
TenantId
OrganizationId
UserId
ProductId
OrderId
Version
AuditInfo
CreatedAt
UpdatedAt
```

But I don't want to.

For example:

```text
ProductId
OrderId
TenantId
OrganizationId
```

are **not universal primitives**.

They belong to their respective contexts.

Later:

```text
catalog
 └── ProductId

sales
 └── OrderId

identity
 └── UserId

organization
 └── OrganizationId

tenant
 └── TenantId
```

This keeps the foundation clean.

---

# 32. One particularly important architectural decision

I recommend that we **do not create a giant `BaseEntity`**.

Avoid this:

```java
public abstract class BaseEntity {

    UUID id;

    Instant createdAt;

    Instant updatedAt;

    UUID tenantId;

    long version;

    String createdBy;

    String updatedBy;

    boolean deleted;

}
```

It looks convenient.

For an ERP platform it eventually becomes a nightmare.

Because suddenly:

```text
Money
Product
Order
JournalEntry
Subscription
Recipe
Task
Student
Warehouse
```

all inherit infrastructure concerns they don't necessarily need.

Instead:

```text
Entity
     ↓
identity only
```

and individual contexts compose additional concerns.

---

# 33. Same principle for auditing

Don't put:

```text
createdAt
updatedAt
createdBy
updatedBy
```

into every domain object yet.

We'll design an **audit model** in the infrastructure/application layer that works with persistence and domain requirements.

This matters because a pure domain model should represent **business state**, not automatically become a database record.

---

# 34. Same principle for tenancy

Don't put:

```java
TenantId tenantId;
```

inside `Entity`.

Eventually we will support:

```text
single tenant
multi tenant
multi company
multi branch
marketplace seller
franchise
```

and those relationships are not identical.

We'll design tenancy properly in the platform layer.

---

# 35. The dependency picture now

This is what we have achieved:

```text
                 JDK
                  ▲
                  │
       ┌──────────┴───────────┐
       │ foundation-domain    │
       │                      │
       │ Entity               │
       │ Aggregate            │
       │ ValueObject          │
       │ Money                │
       │ Quantity             │
       │ Currency             │
       │ DomainEvent          │
       │ DomainException      │
       └──────────────────────┘
```

And later:

```text
                  foundation-domain
                         ▲
             ┌───────────┼───────────┐
             │           │           │
          catalog       sales     accounting
             │           │           │
             └───────────┼───────────┘
                         ▲
                     application
                         ▲
                    infrastructure
                         ▲
                Quarkus / PostgreSQL
                         │
                     Kafka/Redis
```

That's the architecture we want to preserve.

---

# 36. One more thing: architecture tests

Before we move to Phase 2, I'd add **ArchUnit**.

Not to production dependencies—only tests.

Its job will be to enforce things like:

```text
foundation.domain
    MUST NOT depend on:
        Quarkus
        Hibernate
        Kafka
        REST
```

and eventually:

```text
catalog.domain
    MUST NOT depend on:
        order.domain
        accounting.domain
```

This is extremely valuable for the long-term ERP platform because architecture rules otherwise slowly decay.

So Phase 1 should actually finish with:

```text
Foundation
+
Unit tests
+
Architecture tests
```

rather than just Java classes.

---

## Phase 1 final checklist

```text
[x] Maven multi-module foundation
[x] tech.kayys.erp package
[x] DomainId
[x] Entity
[x] AggregateRoot
[x] AbstractAggregateRoot
[x] ValueObject
[x] Money
[x] Currency
[x] Quantity
[x] Unit
[x] Percentage
[x] DateRange
[x] DomainEvent
[x] DomainClock
[x] DomainException
[x] BusinessRuleViolation
[x] InvalidStateException
[x] Unit tests
[ ] Architecture tests
```

**The next step should be the architecture-test layer plus tightening these primitives**—especially `Money`, `Quantity`, aggregate event handling, and typed IDs—before we touch Quarkus.

Then Phase 2 will introduce the **application foundation**: `Command`, `Query`, `Handler`, repository ports, transaction boundary, `Result/Error`, and reactive `Uni` **without contaminating the domain layer**.
