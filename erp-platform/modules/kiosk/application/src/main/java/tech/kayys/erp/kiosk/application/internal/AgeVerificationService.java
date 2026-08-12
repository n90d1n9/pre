package tech.kayys.erp.kiosk.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.kiosk.domain.valueobject.AgeVerificationResult;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Service for age verification using ID scanning.
 */
@Singleton
@UseCase("Verify customer age for restricted items")
public class AgeVerificationService {

    private final IdScannerPort idScannerPort;

    @Inject
    public AgeVerificationService(IdScannerPort idScannerPort) {
        this.idScannerPort = idScannerPort;
    }

    /**
     * Verifies age using ID scanning.
     */
    public CompletionStage<AgeVerificationResult> verifyAge(String idScanData) {
        return idScannerPort.scanId(idScanData)
            .thenApply(idInfo -> {
                // Validate ID data
                if (!idInfo.isValid()) {
                    return AgeVerificationResult.failure("Invalid ID scan data");
                }

                // Calculate age
                int age = calculateAge(idInfo.getDateOfBirth());
                int requiredAge = idInfo.getRequiredAge();

                if (age < requiredAge) {
                    return AgeVerificationResult.failure(
                        "Customer is under " + requiredAge + " years old (Age: " + age + ")"
                    );
                }

                // Check ID expiration
                if (idInfo.isExpired()) {
                    return AgeVerificationResult.failure("ID is expired");
                }

                // Check ID type
                if (!isValidIdType(idInfo.getIdType())) {
                    return AgeVerificationResult.failure("Invalid ID type: " + idInfo.getIdType());
                }

                return AgeVerificationResult.success(
                    age,
                    idInfo.getIdType(),
                    idInfo.getIdNumber(),
                    idScanData
                );
            });
    }

    /**
     * Manually verifies age.
     */
    public AgeVerificationResult verifyAgeManually(int age, int requiredAge) {
        if (age < requiredAge) {
            return AgeVerificationResult.failure(
                "Customer is under " + requiredAge + " years old (Age: " + age + ")"
            );
        }
        return AgeVerificationResult.success(
            age,
            "MANUAL",
            "MANUAL-" + System.currentTimeMillis(),
            null
        );
    }

    private int calculateAge(String dateOfBirth) {
        if (dateOfBirth == null) {
            return -1;
        }
        try {
            LocalDate dob = LocalDate.parse(dateOfBirth);
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean isValidIdType(String idType) {
        return "DRIVERS_LICENSE".equals(idType) ||
               "PASSPORT".equals(idType) ||
               "ID_CARD".equals(idType) ||
               "RESIDENT_CARD".equals(idType);
    }

    /**
     * Port for ID scanning hardware.
     */
    public interface IdScannerPort {
        CompletionStage<IdInfo> scanId(String scanData);
    }

    /**
     * ID information from scanner.
     */
    public static class IdInfo {
        private final boolean valid;
        private final String idType;
        private final String idNumber;
        private final String dateOfBirth;
        private final String expirationDate;
        private final String name;
        private final int requiredAge;

        public IdInfo(
                boolean valid,
                String idType,
                String idNumber,
                String dateOfBirth,
                String expirationDate,
                String name,
                int requiredAge) {
            this.valid = valid;
            this.idType = idType;
            this.idNumber = idNumber;
            this.dateOfBirth = dateOfBirth;
            this.expirationDate = expirationDate;
            this.name = name;
            this.requiredAge = requiredAge;
        }

        public boolean isValid() { return valid; }
        public String getIdType() { return idType; }
        public String getIdNumber() { return idNumber; }
        public String getDateOfBirth() { return dateOfBirth; }
        public String getExpirationDate() { return expirationDate; }
        public String getName() { return name; }
        public int getRequiredAge() { return requiredAge; }

        public boolean isExpired() {
            if (expirationDate == null) {
                return false;
            }
            try {
                LocalDate exp = LocalDate.parse(expirationDate);
                return LocalDate.now().isAfter(exp);
            } catch (Exception e) {
                return true;
            }
        }
    }
}