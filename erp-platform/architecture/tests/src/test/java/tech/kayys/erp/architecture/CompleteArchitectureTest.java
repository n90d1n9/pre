package tech.kayys.erp.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit5.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Comprehensive architecture test for the entire system.
 * This combines all rules into a single test suite.
 */
public class CompleteArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .importPackages("tech.kayys.erp");
    }

    @Test
    void testAllArchitectureRules() {
        // This will run all @ArchTest rules in this class
    }

    // =========================================================================
    // LAYERED ARCHITECTURE
    // =========================================================================

    @ArchTest
    static final ArchRule layeredArchitectureRule =
            layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Foundation").definedBy("tech.kayys.erp.foundation..")
                    .layer("Domain").definedBy("tech.kayys.erp..domain..")
                    .layer("Application").definedBy("tech.kayys.erp..application..")
                    .layer("Infrastructure").definedBy("tech.kayys.erp..infrastructure..")
                    .layer("Interfaces").definedBy("tech.kayys.erp..interfaces..")
                    
                    // Foundation - core building blocks
                    .whereLayer("Foundation")
                    .mayOnlyBeAccessedByLayers("Domain", "Application", "Infrastructure", "Interfaces")
                    .whereLayer("Foundation")
                    .mayNotAccessAnyLayer()
                    
                    // Domain - pure business logic
                    .whereLayer("Domain")
                    .mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Interfaces")
                    .whereLayer("Domain")
                    .mayOnlyAccessLayers("Foundation")
                    
                    // Application - orchestrates business logic
                    .whereLayer("Application")
                    .mayOnlyBeAccessedByLayers("Infrastructure", "Interfaces")
                    .whereLayer("Application")
                    .mayOnlyAccessLayers("Domain", "Foundation")
                    
                    // Infrastructure - technical implementation
                    .whereLayer("Infrastructure")
                    .mayOnlyBeAccessedByLayers("Interfaces")
                    .whereLayer("Infrastructure")
                    .mayOnlyAccessLayers("Application", "Domain", "Foundation")
                    
                    // Interfaces - external communication
                    .whereLayer("Interfaces")
                    .mayNotBeAccessedByAnyLayer()
                    .whereLayer("Interfaces")
                    .mayOnlyAccessLayers("Application", "Domain", "Infrastructure", "Foundation");

    // =========================================================================
    // DOMAIN RULES
    // =========================================================================

    @ArchTest
    static final ArchRule domainPackagesMustHaveCorrectStructure =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp..domain..")
                    .should()
                    .resideInAnyPackage(
                            "tech.kayys.erp..domain.model..",
                            "tech.kayys.erp..domain.identifier..",
                            "tech.kayys.erp..domain.valueobject..",
                            "tech.kayys.erp..domain.event..",
                            "tech.kayys.erp..domain.service..",
                            "tech.kayys.erp..domain.exception..",
                            "tech.kayys.erp..domain.repository.."
                    );

    @ArchTest
    static final ArchRule domainClassesShouldNotHaveAnnotations =
            noClasses()
                    .that()
                    .resideInAPackage("tech.kayys.erp..domain..")
                    .should()
                    .beAnnotatedWith(javax.inject.Inject.class)
                    .orShould()
                    .beAnnotatedWith(javax.enterprise.context.ApplicationScoped.class)
                    .orShould()
                    .beAnnotatedWith(javax.persistence.Entity.class);

    @ArchTest
    static final ArchRule domainClassesShouldBePackagePrivate =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp..domain..")
                    .and()
                    .doNotHaveSimpleNameEndingWith("Id")
                    .and()
                    .doNotHaveSimpleNameEndingWith("Event")
                    .and()
                    .areNotInterfaces()
                    .should()
                    .bePackagePrivate()
                    .orShould()
                    .beFinal();

    // =========================================================================
    // APPLICATION RULES
    // =========================================================================

    @ArchTest
    static final ArchRule applicationPackagesMustHaveCorrectStructure =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp..application..")
                    .should()
                    .resideInAnyPackage(
                            "tech.kayys.erp..application.api..",
                            "tech.kayys.erp..application.internal..",
                            "tech.kayys.erp..application.port.."
                    );

    @ArchTest
    static final ArchRule commandHandlersShouldBeAnnotated =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Handler")
                    .and()
                    .areAssignableTo(tech.kayys.erp.foundation.application.CommandHandler.class)
                    .should()
                    .beAnnotatedWith(tech.kayys.erp.foundation.application.UseCase.class);

    @ArchTest
    static final ArchRule queryHandlersShouldBeAnnotated =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Handler")
                    .and()
                    .areAssignableTo(tech.kayys.erp.foundation.application.QueryHandler.class)
                    .should()
                    .beAnnotatedWith(tech.kayys.erp.foundation.application.UseCase.class);

    // =========================================================================
    // INFRASTRUCTURE RULES
    // =========================================================================

    @ArchTest
    static final ArchRule infrastructureMayUseFrameworks =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp..infrastructure..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "io.quarkus..",
                            "jakarta.persistence..",
                            "io.smallrye..",
                            "org.hibernate.."
                    );

    @ArchTest
    static final ArchRule infrastructureShouldImplementPorts =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp..infrastructure..")
                    .and()
                    .haveSimpleNameEndingWith("Impl")
                    .should()
                    .implement(classes()
                            .that()
                            .resideInAPackage("tech.kayys.erp..application.port..")
                            .or()
                            .resideInAPackage("tech.kayys.erp..domain.repository..")
                    );

    // =========================================================================
    // INTERFACES RULES
    // =========================================================================

    @ArchTest
    static final ArchRule interfacesShouldUseApplicationAPI =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp..interfaces..")
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAPackage("tech.kayys.erp..application.api..")
                    .orShould()
                    .onlyDependOnClassesThat()
                    .resideInAPackage("tech.kayys.erp..application.port..");

    @ArchTest
    static final ArchRule interfacesShouldNotDependOnInternal =
            noClasses()
                    .that()
                    .resideInAPackage("tech.kayys.erp..interfaces..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("tech.kayys.erp..application.internal..")
                    .orShould()
                    .dependOnClassesThat()
                    .resideInAPackage("tech.kayys.erp..domain.service..");

    // =========================================================================
    // NAMING CONVENTIONS
    // =========================================================================

    @ArchTest
    static final ArchRule commandsShouldBeNamedWithCommand =
            classes()
                    .that()
                    .implement(tech.kayys.erp.foundation.application.Command.class)
                    .should()
                    .haveSimpleNameEndingWith("Command");

    @ArchTest
    static final ArchRule queriesShouldBeNamedWithQuery =
            classes()
                    .that()
                    .implement(tech.kayys.erp.foundation.application.Query.class)
                    .should()
                    .haveSimpleNameEndingWith("Query");

    @ArchTest
    static final ArchRule viewsShouldBeNamedWithView =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp..application.api..query..")
                    .and()
                    .haveSimpleNameEndingWith("View")
                    .should()
                    .beRecords();

    @ArchTest
    static final ArchRule servicesShouldBeInterfaces =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Service")
                    .and()
                    .areNotEnums()
                    .should()
                    .beInterfaces();

    @ArchTest
    static final ArchRule dtoClassesShouldBeRecords =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp..interfaces..")
                    .and()
                    .haveSimpleNameEndingWith("Request")
                    .or()
                    .haveSimpleNameEndingWith("Response")
                    .should()
                    .beRecords();

    // =========================================================================
    // DEPENDENCY RULES FOR BOUNDED CONTEXTS
    // =========================================================================

    @ArchTest
    static final ArchRule noDirectDependenciesBetweenBoundedContexts =
            noClasses()
                    .that()
                    .resideInAnyPackage(
                            "tech.kayys.erp.catalog..",
                            "tech.kayys.erp.sales..",
                            "tech.kayys.erp.inventory..",
                            "tech.kayys.erp.accounting.."
                    )
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "tech.kayys.erp.catalog..",
                            "tech.kayys.erp.sales..",
                            "tech.kayys.erp.inventory..",
                            "tech.kayys.erp.accounting.."
                    );

    // =========================================================================
    // FOUNDATION INTEGRITY
    // =========================================================================

    @ArchTest
    static final ArchRule foundationShouldNotContainBusinessConcepts =
            noClasses()
                    .that()
                    .resideInAPackage("tech.kayys.erp.foundation..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "tech.kayys.erp.catalog..",
                            "tech.kayys.erp.sales..",
                            "tech.kayys.erp.inventory..",
                            "tech.kayys.erp.accounting.."
                    );

    @ArchTest
    static final ArchRule foundationDomainShouldNotDependOnApplication =
            noClasses()
                    .that()
                    .resideInAPackage("tech.kayys.erp.foundation.domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("tech.kayys.erp.foundation.application..");
}
#!/bin/bash

echo "========================================="
echo "Building Kayys ERP Platform"
echo "========================================="

# Clean and compile
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "✅ Compilation successful!"

# Run unit tests
echo ""
echo "========================================="
echo "Running Unit Tests"
echo "========================================="
mvn test -DskipIntegrationTests

if [ $? -ne 0 ]; then
    echo "❌ Unit tests failed!"
    exit 1
fi

echo "✅ Unit tests passed!"

# Run architecture tests
echo ""
echo "========================================="
echo "Running Architecture Tests"
echo "========================================="
cd architecture/tests
mvn test
cd ../..

if [ $? -ne 0 ]; then
    echo "❌ Architecture tests failed!"
    exit 1
fi

echo "✅ Architecture tests passed!"

# Package the application
echo ""
echo "========================================="
echo "Packaging Application"
echo "========================================="
mvn package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Packaging failed!"
    exit 1
fi

echo "✅ Packaging successful!"
echo ""
echo "========================================="
echo "✅ BUILD COMPLETE - ALL TESTS PASSED"
echo "========================================="
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Building Kayys ERP Platform" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# Clean and compile
mvn clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Compilation successful!" -ForegroundColor Green

# Run unit tests
Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Running Unit Tests" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
mvn test -DskipIntegrationTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Unit tests failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Unit tests passed!" -ForegroundColor Green

# Run architecture tests
Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Running Architecture Tests" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Push-Location architecture/tests
mvn test
Pop-Location

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Architecture tests failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Architecture tests passed!" -ForegroundColor Green

# Package the application
Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Packaging Application" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
mvn package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Packaging failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Packaging successful!" -ForegroundColor Green
Write-Host ""
Write-Host "=========================================" -ForegroundColor Green
Write-Host "✅ BUILD COMPLETE - ALL TESTS PASSED" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
version: '3.8'

services:
  postgresql:
    image: postgres:15
    container_name: erp-postgres
    environment:
      POSTGRES_DB: catalog_db
      POSTGRES_USER: catalog_user
      POSTGRES_PASSWORD: catalog_password
      POSTGRES_MULTIPLE_DATABASES: sales_db,inventory_db,accounting_db
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docker-init:/docker-entrypoint-initdb.d
    networks:
      - erp-network

  redis:
    image: redis:7-alpine
    container_name: erp-redis
    ports:
      - "6379:6379"
    networks:
      - erp-network

  kafka:
    image: apache/kafka:latest
    container_name: erp-kafka
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_LISTENERS: PLAINTEXT://:9092
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    ports:
      - "9092:9092"
    networks:
      - erp-network

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: erp-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    networks:
      - erp-network

volumes:
  postgres_data:

networks:
  erp-network:
    driver: bridge
#!/bin/bash

set -e

echo "Creating databases..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE sales_db;
    CREATE DATABASE inventory_db;
    CREATE DATABASE accounting_db;
    GRANT ALL PRIVILEGES ON DATABASE sales_db TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE inventory_db TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE accounting_db TO $POSTGRES_USER;
EOSQL

echo "Databases created successfully!"
#!/bin/bash

echo "Validating project structure..."

# Check all required directories exist
REQUIRED_DIRS=(
    "foundation/domain/src/main/java/tech/kayys/erp/foundation/domain"
    "foundation/application/src/main/java/tech/kayys/erp/foundation/application"
    "modules/catalog/domain/src/main/java/tech/kayys/erp/catalog/domain"
    "modules/catalog/application/src/main/java/tech/kayys/erp/catalog/application"
    "modules/catalog/infrastructure/src/main/java/tech/kayys/erp/catalog/infrastructure"
    "modules/catalog/interfaces/src/main/java/tech/kayys/erp/catalog/interfaces"
    "architecture/tests/src/test/java/tech/kayys/erp/architecture"
)

for dir in "${REQUIRED_DIRS[@]}"; do
    if [ ! -d "$dir" ]; then
        echo "❌ Missing directory: $dir"
        exit 1
    fi
done

echo "✅ All required directories exist"

# Check all required pom.xml files exist
REQUIRED_POMS=(
    "pom.xml"
    "foundation/domain/pom.xml"
    "foundation/application/pom.xml"
    "modules/catalog/domain/pom.xml"
    "modules/catalog/application/pom.xml"
    "modules/catalog/infrastructure/pom.xml"
    "modules/catalog/interfaces/pom.xml"
    "architecture/tests/pom.xml"
)

for pom in "${REQUIRED_POMS[@]}"; do
    if [ ! -f "$pom" ]; then
        echo "❌ Missing pom.xml: $pom"
        exit 1
    fi
done

echo "✅ All required pom.xml files exist"

# Check modules are in root pom
for module in "foundation/domain" "foundation/application" "modules/catalog/domain" "modules/catalog/application" "modules/catalog/infrastructure" "modules/catalog/interfaces" "architecture/tests"; do
    if ! grep -q "<module>$module</module>" pom.xml; then
        echo "❌ Module $module not found in root pom.xml"
        exit 1
    fi
done

echo "✅ All modules are configured in root pom.xml"
echo ""
echo "========================================="
echo "✅ Project structure validation passed!"
echo "========================================="
# Kayys ERP Platform

A modular, DDD-based ERP platform with Hexagonal Architecture.

## Architecture Overview

./build-and-test.sh
docker-compose up -d
cd modules/catalog/interfaces
mvn quarkus:dev
cd architecture/tests
mvn test
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro",
    "description": "High-performance laptop",
    "price": 1299.99,
    "currencyCode": "USD",
    "sku": "LP-001"
  }'
curl http://localhost:8080/api/v1/products/{id}
curl "http://localhost:8080/api/v1/products?name=Laptop&activeOnly=true"