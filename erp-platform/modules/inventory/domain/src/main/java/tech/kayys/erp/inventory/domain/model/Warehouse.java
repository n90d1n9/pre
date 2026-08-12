package tech.kayys.erp.inventory.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.inventory.domain.identifier.WarehouseId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Warehouse aggregate root.
 * Represents a physical or virtual warehouse location.
 */
public final class Warehouse extends AggregateRoot<WarehouseId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String managerId;
    private int capacity;
    private int currentStockCount;
    private boolean active;
    private boolean defaultWarehouse;
    private List<String> zones;

    private Warehouse(WarehouseId id) {
        super(id);
        this.zones = new ArrayList<>();
        this.active = true;
        this.currentStockCount = 0;
    }

    private Warehouse() {
        super();
    }

    /**
     * Factory method to create a new warehouse.
     */
    public static Warehouse create(
            WarehouseId id,
            String code,
            String name,
            String address,
            String city,
            String country) {
        Warehouse warehouse = new Warehouse(id);
        warehouse.code = code;
        warehouse.name = name;
        warehouse.address = address;
        warehouse.city = city;
        warehouse.country = country;
        return warehouse;
    }

    /**
     * Adds a zone to the warehouse.
     */
    public void addZone(String zone) {
        if (!zones.contains(zone)) {
            zones.add(zone);
            setUpdatedAt(java.time.Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a zone from the warehouse.
     */
    public void removeZone(String zone) {
        zones.remove(zone);
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Updates stock count.
     */
    public void updateStockCount(int change) {
        this.currentStockCount += change;
        if (this.currentStockCount < 0) {
            this.currentStockCount = 0;
        }
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Activates the warehouse.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the warehouse.
     */
    public void deactivate() {
        if (defaultWarehouse) {
            throw new IllegalStateException("Cannot deactivate default warehouse");
        }
        this.active = false;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Sets as default warehouse.
     */
    public void setAsDefault() {
        this.defaultWarehouse = true;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    /**
     * Gets the warehouse utilization percentage.
     */
    public double getUtilization() {
        if (capacity == 0) {
            return 0.0;
        }
        return (double) currentStockCount / capacity * 100.0;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getManagerId() { return managerId; }
    public int getCapacity() { return capacity; }
    public int getCurrentStockCount() { return currentStockCount; }
    public boolean isActive() { return active; }
    public boolean isDefaultWarehouse() { return defaultWarehouse; }
    public List<String> getZones() { return Collections.unmodifiableList(zones); }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setState(String state) {
        this.state = state;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setEmail(String email) {
        this.email = email;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        setUpdatedAt(java.time.Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", stockCount=" + currentStockCount +
                '}';
    }
}