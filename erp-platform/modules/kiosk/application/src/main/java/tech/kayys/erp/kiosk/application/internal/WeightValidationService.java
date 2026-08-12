package tech.kayys.erp.kiosk.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.kiosk.domain.valueobject.WeightValidation;
import tech.kayys.erp.groceries.domain.valueobject.Weight;
import tech.kayys.erp.kiosk.domain.model.ScaleDevice;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Service for weight validation in self-checkout.
 */
@Singleton
@UseCase("Validate item weights in self-checkout")
public class WeightValidationService {

    private final ScaleManager scaleManager;

    @Inject
    public WeightValidationService(ScaleManager scaleManager) {
        this.scaleManager = scaleManager;
    }

    /**
     * Validates the weight of an item at self-checkout.
     */
    public CompletionStage<WeightValidation> validateWeight(
            String productId,
            String scaleId,
            Weight expectedWeight,
            double tolerancePercent) {
        
        return scaleManager.readWeight(scaleId)
            .thenApply(actualWeight -> {
                // Validate that the scale is ready
                if (actualWeight == null || actualWeight.isZero()) {
                    return WeightValidation.failure(
                        productId,
                        expectedWeight,
                        actualWeight != null ? actualWeight : Weight.zero(),
                        "Scale reading is invalid or zero"
                    );
                }

                // Check if weight is within tolerance
                double diffPercent = calculateDifferencePercent(expectedWeight, actualWeight);
                
                if (Math.abs(diffPercent) <= tolerancePercent) {
                    return WeightValidation.success(productId, expectedWeight, actualWeight);
                } else {
                    return WeightValidation.failure(
                        productId,
                        expectedWeight,
                        actualWeight,
                        String.format(
                            "Weight mismatch: Expected %.2fg, Got %.2fg (%.1f%% difference)",
                            expectedWeight.toGrams().doubleValue(),
                            actualWeight.toGrams().doubleValue(),
                            diffPercent
                        )
                    );
                }
            });
    }

    /**
     * Validates multiple items in the checkout bagging area.
     */
    public CompletionStage<List<WeightValidation>> validateBaggingArea(
            List<WeightValidation> expectedItems,
            String scaleId) {
        
        return scaleManager.readWeight(scaleId)
            .thenApply(totalWeight -> {
                List<WeightValidation> results = new ArrayList<>();
                
                // Calculate expected total weight
                Weight expectedTotal = expectedItems.stream()
                    .map(WeightValidation::getScannedWeight)
                    .reduce(Weight.zero(), Weight::add);
                
                // Check if total matches
                double diffPercent = calculateDifferencePercent(expectedTotal, totalWeight);
                
                if (Math.abs(diffPercent) <= 5.0) {
                    // All items validated
                    for (WeightValidation item : expectedItems) {
                        results.add(WeightValidation.success(
                            item.getProductId(),
                            item.getScannedWeight(),
                            item.getActualWeight()
                        ));
                    }
                } else {
                    // Individual validation needed
                    for (WeightValidation item : expectedItems) {
                        if (item.isValidated()) {
                            results.add(item);
                        } else {
                            results.add(WeightValidation.failure(
                                item.getProductId(),
                                item.getScannedWeight(),
                                item.getActualWeight(),
                                "Item weight mismatch in bagging area"
                            ));
                        }
                    }
                }
                
                return results;
            });
    }

    private double calculateDifferencePercent(Weight expected, Weight actual) {
        if (expected == null || expected.isZero()) {
            return 0.0;
        }
        double expectedGrams = expected.toGrams().doubleValue();
        double actualGrams = actual.toGrams().doubleValue();
        return ((actualGrams - expectedGrams) / expectedGrams) * 100.0;
    }
}