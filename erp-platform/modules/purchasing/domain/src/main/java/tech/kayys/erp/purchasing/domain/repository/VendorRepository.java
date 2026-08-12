package tech.kayys.erp.purchasing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.Vendor;
import tech.kayys.erp.purchasing.domain.valueobject.VendorStatus;
import tech.kayys.erp.purchasing.domain.valueobject.VendorType;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Vendor aggregates.
 */
public interface VendorRepository extends Repository<Vendor, VendorId> {

    /**
     * Finds vendors by type.
     */
    CompletionStage<List<Vendor>> findByType(VendorType type);

    /**
     * Finds active vendors.
     */
    CompletionStage<List<Vendor>> findActiveVendors();

    /**
     * Finds vendors by status.
     */
    CompletionStage<List<Vendor>> findByStatus(VendorStatus status);

    /**
     * Finds vendors by name containing text.
     */
    CompletionStage<List<Vendor>> findByNameContaining(String name);

    /**
     * Finds vendors with high performance rating.
     */
    CompletionStage<List<Vendor>> findTopRatedVendors(int limit);

    /**
     * Checks if a vendor exists by name.
     */
    CompletionStage<Boolean> existsByName(String name);
}