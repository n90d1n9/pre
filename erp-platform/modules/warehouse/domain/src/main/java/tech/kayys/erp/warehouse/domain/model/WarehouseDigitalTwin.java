package tech.kayys.erp.warehouse.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Warehouse Digital Twin aggregate root.
 * Represents a virtual replica of the physical warehouse for simulation and optimization.
 */
public final class WarehouseDigitalTwin extends AggregateRoot<WarehouseId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String version;
    private List<VirtualBin> virtualBins;
    private List<VirtualZone> virtualZones;
    private List<VirtualAisle> virtualAisles;
    private List<VirtualEquipment> virtualEquipment;
    private Map<String, Object> layoutData;
    private Map<String, Object> simulationParameters;
    private String status; // ACTIVE, UPDATING, SYNCING, OFFLINE
    private Instant lastSyncTime;
    private Instant lastSimulationRun;
    private double accuracyScore;
    private String notes;
    private boolean active;

    private WarehouseDigitalTwin(WarehouseId id) {
        super(id);
        this.virtualBins = new ArrayList<>();
        this.virtualZones = new ArrayList<>();
        this.virtualAisles = new ArrayList<>();
        this.virtualEquipment = new ArrayList<>();
        this.status = "ACTIVE";
        this.active = true;
        this.version = "1.0";
        this.accuracyScore = 95.0;
    }

    private WarehouseDigitalTwin() {
        super();
    }

    /**
     * Factory method to create a new digital twin.
     */
    public static WarehouseDigitalTwin create(
            WarehouseId id,
            String name,
            Map<String, Object> layoutData) {
        WarehouseDigitalTwin twin = new WarehouseDigitalTwin(id);
        twin.name = name;
        twin.layoutData = layoutData;
        twin.lastSyncTime = Instant.now();
        return twin;
    }

    /**
     * Adds a virtual bin to the digital twin.
     */
    public void addVirtualBin(VirtualBin bin) {
        virtualBins.add(bin);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a virtual zone to the digital twin.
     */
    public void addVirtualZone(VirtualZone zone) {
        virtualZones.add(zone);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a virtual aisle to the digital twin.
     */
    public void addVirtualAisle(VirtualAisle aisle) {
        virtualAisles.add(aisle);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds virtual equipment to the digital twin.
     */
    public void addVirtualEquipment(VirtualEquipment equipment) {
        virtualEquipment.add(equipment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the digital twin from physical warehouse data.
     */
    public void syncFromPhysical(Map<String, Object> physicalData) {
        this.layoutData = physicalData;
        this.lastSyncTime = Instant.now();
        this.status = "SYNCING";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Runs a simulation on the digital twin.
     */
    public void runSimulation(Map<String, Object> simulationParams) {
        this.simulationParameters = simulationParams;
        this.lastSimulationRun = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the accuracy score.
     */
    public void updateAccuracy(double accuracyScore) {
        this.accuracyScore = accuracyScore;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the digital twin.
     */
    public void activate() {
        this.active = true;
        this.status = "ACTIVE";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the digital twin.
     */
    public void deactivate() {
        this.active = false;
        this.status = "OFFLINE";
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the total virtual bin count.
     */
    public int getVirtualBinCount() {
        return virtualBins.size();
    }

    /**
     * Gets the total virtual zone count.
     */
    public int getVirtualZoneCount() {
        return virtualZones.size();
    }

    /**
     * Gets the utilization of the digital twin.
     */
    public double getUtilization() {
        if (virtualBins.isEmpty()) {
            return 0.0;
        }
        long occupiedBins = virtualBins.stream()
            .filter(VirtualBin::isOccupied)
            .count();
        return (double) occupiedBins / virtualBins.size() * 100.0;
    }

    // Getters
    public String getName() { return name; }
    public String getVersion() { return version; }
    public List<VirtualBin> getVirtualBins() { return Collections.unmodifiableList(virtualBins); }
    public List<VirtualZone> getVirtualZones() { return Collections.unmodifiableList(virtualZones); }
    public List<VirtualAisle> getVirtualAisles() { return Collections.unmodifiableList(virtualAisles); }
    public List<VirtualEquipment> getVirtualEquipment() { return Collections.unmodifiableList(virtualEquipment); }
    public Map<String, Object> getLayoutData() { return layoutData; }
    public Map<String, Object> getSimulationParameters() { return simulationParameters; }
    public String getStatus() { return status; }
    public Instant getLastSyncTime() { return lastSyncTime; }
    public Instant getLastSimulationRun() { return lastSimulationRun; }
    public double getAccuracyScore() { return accuracyScore; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "WarehouseDigitalTwin{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", status='" + status + '\'' +
                ", accuracy=" + accuracyScore + "%" +
                ", bins=" + virtualBins.size() +
                '}';
    }

    /**
     * Virtual bin value object.
     */
    public static final class VirtualBin implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String code;
        private final String zone;
        private final String aisle;
        private final String level;
        private final String position;
        private final double xCoordinate;
        private final double yCoordinate;
        private final double zCoordinate;
        private final double capacity;
        private double occupied;
        private String productId;
        private String productName;
        private boolean occupied;

        public VirtualBin(
                String id,
                String code,
                String zone,
                String aisle,
                String level,
                String position,
                double xCoordinate,
                double yCoordinate,
                double zCoordinate,
                double capacity) {
            this.id = id;
            this.code = code;
            this.zone = zone;
            this.aisle = aisle;
            this.level = level;
            this.position = position;
            this.xCoordinate = xCoordinate;
            this.yCoordinate = yCoordinate;
            this.zCoordinate = zCoordinate;
            this.capacity = capacity;
            this.occupied = 0.0;
            this.occupied = false;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Bin ID cannot be empty");
            }
            if (capacity <= 0) {
                throw new IllegalArgumentException("Capacity must be positive");
            }
        }

        public String getId() { return id; }
        public String getCode() { return code; }
        public String getZone() { return zone; }
        public String getAisle() { return aisle; }
        public String getLevel() { return level; }
        public String getPosition() { return position; }
        public double getXCoordinate() { return xCoordinate; }
        public double getYCoordinate() { return yCoordinate; }
        public double getZCoordinate() { return zCoordinate; }
        public double getCapacity() { return capacity; }
        public double getOccupied() { return occupied; }
        public double getAvailable() { return capacity - occupied; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public boolean isOccupied() { return occupied; }

        public void occupy(String productId, String productName, double quantity) {
            if (quantity > getAvailable()) {
                throw new IllegalArgumentException("Insufficient capacity");
            }
            this.productId = productId;
            this.productName = productName;
            this.occupied += quantity;
            this.occupied = true;
        }

        public void free(double quantity) {
            if (quantity > occupied) {
                throw new IllegalArgumentException("Cannot free more than occupied");
            }
            this.occupied -= quantity;
            if (this.occupied <= 0) {
                this.occupied = 0;
                this.occupied = false;
                this.productId = null;
                this.productName = null;
            }
        }

        public double getUtilization() {
            if (capacity == 0) {
                return 0.0;
            }
            return occupied / capacity * 100.0;
        }

        public String getFullLocation() {
            return zone + "-" + aisle + "-" + level + "-" + position;
        }

        @Override
        public String toString() {
            return "VirtualBin{" +
                    "code='" + code + '\'' +
                    ", location='" + getFullLocation() + '\'' +
                    ", utilization=" + getUtilization() + "%" +
                    '}';
        }
    }

    /**
     * Virtual zone value object.
     */
    public static final class VirtualZone implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String name;
        private final String description;
        private final double xStart;
        private final double yStart;
        private final double xEnd;
        private final double yEnd;
        private final String type; // RECEIVING, STORAGE, PICKING, SHIPPING

        public VirtualZone(
                String id,
                String name,
                String description,
                double xStart,
                double yStart,
                double xEnd,
                double yEnd,
                String type) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.xStart = xStart;
            this.yStart = yStart;
            this.xEnd = xEnd;
            this.yEnd = yEnd;
            this.type = type;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Zone ID cannot be empty");
            }
            if (xEnd < xStart || yEnd < yStart) {
                throw new IllegalArgumentException("Invalid zone coordinates");
            }
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getXStart() { return xStart; }
        public double getYStart() { return yStart; }
        public double getXEnd() { return xEnd; }
        public double getYEnd() { return yEnd; }
        public String getType() { return type; }
        public double getArea() { return (xEnd - xStart) * (yEnd - yStart); }

        @Override
        public String toString() {
            return "VirtualZone{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", area=" + getArea() +
                    '}';
        }
    }

    /**
     * Virtual aisle value object.
     */
    public static final class VirtualAisle implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String name;
        private final String zone;
        private final double xStart;
        private final double yStart;
        private final double xEnd;
        private final double yEnd;

        public VirtualAisle(
                String id,
                String name,
                String zone,
                double xStart,
                double yStart,
                double xEnd,
                double yEnd) {
            this.id = id;
            this.name = name;
            this.zone = zone;
            this.xStart = xStart;
            this.yStart = yStart;
            this.xEnd = xEnd;
            this.yEnd = yEnd;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Aisle ID cannot be empty");
            }
            if (xEnd < xStart || yEnd < yStart) {
                throw new IllegalArgumentException("Invalid aisle coordinates");
            }
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getZone() { return zone; }
        public double getXStart() { return xStart; }
        public double getYStart() { return yStart; }
        public double getXEnd() { return xEnd; }
        public double getYEnd() { return yEnd; }

        public double getLength() {
            return Math.sqrt(Math.pow(xEnd - xStart, 2) + Math.pow(yEnd - yStart, 2));
        }

        @Override
        public String toString() {
            return "VirtualAisle{" +
                    "name='" + name + '\'' +
                    ", zone='" + zone + '\'' +
                    ", length=" + getLength() +
                    '}';
        }
    }

    /**
     * Virtual equipment value object.
     */
    public static final class VirtualEquipment implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String name;
        private final String type; // FORKLIFT, CONVEYOR, AGV, ROBOT, SCANNER
        private final String status; // OPERATIONAL, MAINTENANCE, OFFLINE
        private final double xCoordinate;
        private final double yCoordinate;
        private final double zCoordinate;
        private final String zone;

        public VirtualEquipment(
                String id,
                String name,
                String type,
                String status,
                double xCoordinate,
                double yCoordinate,
                double zCoordinate,
                String zone) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.status = status;
            this.xCoordinate = xCoordinate;
            this.yCoordinate = yCoordinate;
            this.zCoordinate = zCoordinate;
            this.zone = zone;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Equipment ID cannot be empty");
            }
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Equipment type cannot be empty");
            }
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public double getXCoordinate() { return xCoordinate; }
        public double getYCoordinate() { return yCoordinate; }
        public double getZCoordinate() { return zCoordinate; }
        public String getZone() { return zone; }

        public boolean isOperational() {
            return "OPERATIONAL".equals(status);
        }

        @Override
        public String toString() {
            return "VirtualEquipment{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", status='" + status + '\'' +
                    ", zone='" + zone + '\'' +
                    '}';
        }
    }
}