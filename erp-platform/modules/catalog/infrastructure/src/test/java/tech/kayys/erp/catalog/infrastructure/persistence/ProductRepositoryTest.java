package tech.kayys.erp.catalog.infrastructure.persistence;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@Testcontainers
@TestProfile(TestContainersProfile.class)
public class ProductRepositoryTest {

    @Inject
    ProductRepositoryImpl productRepository;

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    static {
        postgres.start();
        System.setProperty("quarkus.datasource.reactive.url", 
            "postgresql://" + postgres.getHost() + ":" + postgres.getMappedPort(5432) + "/testdb");
        System.setProperty("quarkus.datasource.username", postgres.getUsername());
        System.setProperty("quarkus.datasource.password", postgres.getPassword());
    }

    @BeforeEach
    void setUp() {
        // Clean up before each test
    }

    @Test
    void testCreateProduct() {
        Product product = Product.create(
            ProductId.generate(),
            "Test Product",
            "Test Description",
            Money.of(new BigDecimal("29.99"), "USD"),
            "TEST-SKU-001"
        );

        Uni<Product> saved = productRepository.save(product);
        Product result = saved.await().indefinitely();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getSku()).isEqualTo("TEST-SKU-001");
        assertThat(result.getPrice().getAmount()).isEqualByComparingTo(new BigDecimal("29.99"));
    }

    @Test
    void testFindById() {
        // Create product first
        Product product = Product.create(
            ProductId.generate(),
            "Find Product",
            "Find Description",
            Money.of(new BigDecimal("19.99"), "USD"),
            "FIND-SKU-001"
        );

        Product saved = productRepository.save(product).await().indefinitely();
        
        Uni<Optional<Product>> found = productRepository.findById(saved.getId());
        Optional<Product> result = found.await().indefinitely();

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Find Product");
    }

    @Test
    void testExistsBySku() {
        Product product = Product.create(
            ProductId.generate(),
            "Sku Product",
            "Sku Description",
            Money.of(new BigDecimal("9.99"), "USD"),
            "SKU-TEST-001"
        );

        productRepository.save(product).await().indefinitely();
        
        Uni<Boolean> exists = productRepository.existsBySku("SKU-TEST-001");
        Boolean result = exists.await().indefinitely();

        assertThat(result).isTrue();
    }

    @Test
    void testSearchProducts() {
        // Create test products
        for (int i = 1; i <= 5; i++) {
            Product product = Product.create(
                ProductId.generate(),
                "Search Product " + i,
                "This is searchable content",
                Money.of(new BigDecimal("10.00"), "USD"),
                "SEARCH-SKU-" + i
            );
            productRepository.save(product).await().indefinitely();
        }

        Uni<List<Product>> results = productRepository.searchProducts("searchable");
        List<Product> found = results.await().indefinitely();

        assertThat(found).hasSize(5);
    }
}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-catalog-infrastructure</artifactId>

    <dependencies>
        <!-- Application and Domain -->
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-catalog-application</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-catalog-domain</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Foundation Persistence -->
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-persistence</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Quarkus Reactive -->
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-hibernate-reactive-panache</artifactId>
        </dependency>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-reactive-pg-client</artifactId>
        </dependency>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-smallrye-reactive-messaging</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-junit5</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-testcontainers</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>io.quarkus</groupId>
                <artifactId>quarkus-maven-plugin</artifactId>
                <version>${quarkus.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>build</goal>
                            <goal>generate-code</goal>
                            <goal>generate-code-tests</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>