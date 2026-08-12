package tech.kayys.erp.warehouse.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;
import tech.kayys.erp.warehouse.domain.valueobject.BinStatus;
import tech.kayys.erp.warehouse.domain.valueobject.BinType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bin location entity for persistence.
 */
@Entity
@Table(name = "bin_locations", indexes = {
    @Index(name = "idx_bin_code", columnList = "code"),
    @Index(name = "idx_bin_warehouse", columnList = "warehouse_id"),
    @Index(name = "idx_bin_type", columnList = "bin_type"),
    @Index(name = "idx_bin_status", columnList = "status"),
    @Index(name = "idx_bin_zone", columnList = "zone")
})
public class BinLocationEntity extends BaseEntity {

    @Column(name = "warehouse_id", nullable = false, columnDefinition = "UUID")
    public UUID warehouseId;

    @Column(name = "code", nullable = false, length = 50)
    public String code;

    @Column(name = "name", length = 100)
    public String name;

    @Column(name = "description", length = 500)
    public String description;

    @Column(name = "bin_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public BinType binType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public BinStatus status;

    @Column(name = "zone", length = 50)
    public String zone;

    @Column(name = "aisle", length = 50)
    public String aisle;

    @Column(name = "level", length = 20)
    public String level;

    @Column(name = "position", length = 20)
    public String position;

    @Column(name = "capacity", nullable = false)
    public int capacity;

    @Column(name = "occupied", nullable = false)
    public int occupied;

    @Column(name = "max_weight")
    public int maxWeight;

    @Column(name = "max_length")
    public int maxLength;

    @Column(name = "max_width")
    public int maxWidth;

    @Column(name = "max_height")
    public int maxHeight;

    @Column(name = "is_active", nullable = false)
    public boolean active = true;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "bin_assigned_products", joinColumns = @JoinColumn(name = "bin_location_id"))
    @Column(name = "product_id", columnDefinition = "UUID")
    public List<UUID> assignedProductIds = new ArrayList<>();
}