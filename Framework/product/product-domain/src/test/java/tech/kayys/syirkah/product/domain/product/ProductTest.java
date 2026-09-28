package tech.kayys.syirkah.product.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.kayys.syirkah.foundation.domain.exception.BusinessRuleViolation;
import tech.kayys.syirkah.foundation.domain.exception.InvalidStateException;
import tech.kayys.syirkah.product.domain.event.ProductActivated;
import tech.kayys.syirkah.product.domain.event.ProductArchived;
import tech.kayys.syirkah.product.domain.event.ProductCreated;
import tech.kayys.syirkah.product.domain.event.ProductDiscontinued;
import tech.kayys.syirkah.product.domain.event.ProductIdentifierAdded;
import tech.kayys.syirkah.product.domain.event.ProductIdentifierRemoved;
import tech.kayys.syirkah.product.domain.event.ProductRenamed;
import tech.kayys.syirkah.product.domain.identifier.IdentifierType;
import tech.kayys.syirkah.product.domain.identifier.ProductIdentifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Product aggregate")
class ProductTest {

    private static Product newProduct() {
        return Product.create(
                ProductId.generate(),
                "COFFEE-LATTE",
                "Latte",
                "Fresh milk coffee",
                ProductType.PHYSICAL
        );
    }

    @Test
    void createsProductInDraftState() {
        ProductId id = ProductId.generate();

        var product = Product.create(
                id,
                "COFFEE-LATTE",
                "Latte",
                "Fresh milk coffee",
                ProductType.PHYSICAL
        );

        assertEquals(id, product.id());
        assertEquals(ProductStatus.DRAFT, product.status());
        assertEquals("COFFEE-LATTE", product.code());
        assertEquals("Latte", product.name());
        assertEquals("Fresh milk coffee", product.description());
        assertEquals(ProductType.PHYSICAL, product.type());
        assertTrue(product.identifiers().isEmpty());
    }

    @Test
    void creationRaisesProductCreated() {
        var product = newProduct();

        var events = product.pullDomainEvents();

        assertEquals(1, events.size());

        var created = assertInstanceOf(
                ProductCreated.class,
                events.getFirst()
        );

        assertEquals(product.id(), created.productId());
        assertEquals("COFFEE-LATTE", created.code());
        assertEquals("product.product-created", created.eventType());
    }

    @Test
    void pullsAndClearsEvents() {
        var product = newProduct();

        product.pullDomainEvents();

        assertTrue(product.pullDomainEvents().isEmpty());
    }

    @Test
    void activatesDraftProduct() {
        var product = newProduct();

        product.pullDomainEvents();
        product.activate();

        assertEquals(ProductStatus.ACTIVE, product.status());

        var events = product.pullDomainEvents();

        assertEquals(1, events.size());
        assertInstanceOf(ProductActivated.class, events.getFirst());
    }

    @Test
    void rejectsActivatingTwice() {
        var product = newProduct();

        product.activate();

        assertThrows(InvalidStateException.class, product::activate);
    }

    @Test
    void rejectsDiscontinuingDraftProduct() {
        var product = newProduct();

        assertThrows(
                InvalidStateException.class,
                product::discontinue
        );
    }

    @Test
    void rejectsArchivingActiveProduct() {
        var product = newProduct();

        product.activate();

        assertThrows(InvalidStateException.class, product::archive);
    }

    @Test
    void followsFullLifecycleWithEvents() {
        var product = newProduct();

        product.pullDomainEvents();

        product.activate();
        product.discontinue();
        product.archive();

        assertEquals(ProductStatus.ARCHIVED, product.status());

        var events = product.pullDomainEvents();

        assertEquals(3, events.size());
        assertInstanceOf(ProductActivated.class, events.get(0));
        assertInstanceOf(ProductDiscontinued.class, events.get(1));
        assertInstanceOf(ProductArchived.class, events.get(2));
    }

    @Test
    void archivedProductCannotBeModified() {
        var product = newProduct();

        product.activate();
        product.discontinue();
        product.archive();

        assertThrows(
                InvalidStateException.class,
                () -> product.rename("New name")
        );

        assertThrows(
                InvalidStateException.class,
                () -> product.changeDescription("New description")
        );

        assertThrows(
                InvalidStateException.class,
                () -> product.addIdentifier(
                        new ProductIdentifier(
                                IdentifierType.EAN,
                                "8991234567890"
                        )
                )
        );
    }

    @Test
    void renameRaisesEventOnlyWhenNameChanges() {
        var product = newProduct();

        product.pullDomainEvents();

        product.rename("Latte");
        assertTrue(product.pullDomainEvents().isEmpty());

        product.rename("Caffe Latte");

        assertEquals("Caffe Latte", product.name());

        var renamed = assertInstanceOf(
                ProductRenamed.class,
                product.pullDomainEvents().getFirst()
        );

        assertEquals("Latte", renamed.oldName());
        assertEquals("Caffe Latte", renamed.newName());
    }

    @Test
    void changeDescriptionRaisesUpdatedEvent() {
        var product = newProduct();

        product.pullDomainEvents();
        product.changeDescription("Stronger coffee");

        assertEquals("Stronger coffee", product.description());
        assertEquals(1, product.pullDomainEvents().size());
    }

    @Test
    void addsAndRemovesIdentifiers() {
        var product = newProduct();

        product.pullDomainEvents();

        var ean = new ProductIdentifier(
                IdentifierType.EAN,
                "8991234567890"
        );

        product.addIdentifier(ean);

        assertEquals(1, product.identifiers().size());
        assertInstanceOf(
                ProductIdentifierAdded.class,
                product.pullDomainEvents().getFirst()
        );

        product.removeIdentifier(ean);

        assertTrue(product.identifiers().isEmpty());
        assertInstanceOf(
                ProductIdentifierRemoved.class,
                product.pullDomainEvents().getFirst()
        );
    }

    @Test
    void rejectsDuplicateIdentifier() {
        var product = newProduct();

        var ean = new ProductIdentifier(
                IdentifierType.EAN,
                "8991234567890"
        );

        product.addIdentifier(ean);

        assertThrows(
                BusinessRuleViolation.class,
                () -> product.addIdentifier(ean)
        );
    }

    @Test
    void rejectsRemovingUnknownIdentifier() {
        var product = newProduct();

        assertThrows(
                BusinessRuleViolation.class,
                () -> product.removeIdentifier(
                        new ProductIdentifier(
                                IdentifierType.EAN,
                                "0000000000000"
                        )
                )
        );
    }

    @Test
    void rejectsBlankCodeAndNullType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Product.create(
                        ProductId.generate(),
                        " ",
                        "Latte",
                        null,
                        ProductType.PHYSICAL
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> Product.create(
                        ProductId.generate(),
                        "COFFEE-LATTE",
                        "Latte",
                        null,
                        null
                )
        );
    }
}