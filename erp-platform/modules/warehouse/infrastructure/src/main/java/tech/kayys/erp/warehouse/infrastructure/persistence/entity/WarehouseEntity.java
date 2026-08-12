package tech.kayys.erp.warehouse.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Warehouse entity for persistence.
 */
@Entity
@Table(name = "warehouses", indexes = {
    @Index(name = "idx_warehouse_code", columnList = "code"),
    @Index(name = "idx_warehouse_name", columnList = "name"),
    @Index(name = "idx_warehouse_default", columnList = "is_default")
})
public class WarehouseEntity extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    public String code;

    @Column(name = "name", nullable = false, length = 100)
    public String name;

    @Column(name = "description", length = 500)
    public String description;

    @Column(name = "address", length = 255)
    public String address;

    @Column(name = "city", length = 50)
    public String city;

    @Column(name = "state", length = 50)
    public String state;

    @Column(name = "postal_code", length = 20)
    public String postalCode;

    @Column(name = "country", length = 50)
    public String country;

    @Column(name = "phone", length = 20)
    public String phone;

    @Column(name = "email", length = 100)
    public String email;

    @Column(name = "manager_id", columnDefinition = "UUID")
    public UUID managerId;

    @Column(name = "capacity")
    public int capacity;

    @Column(name = "current_stock_count")
    public int currentStockCount;

    @Column(name = "is_active", nullable = false)
    public boolean active = true;

    @Column(name = "is_default", nullable = false)
    public boolean defaultWarehouse = false;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "warehouse_zones", joinColumns = @JoinColumn(name = "warehouse_id"))
    @Column(name = "zone", length = 50)
    public List<String> zones = new ArrayList<>();
}