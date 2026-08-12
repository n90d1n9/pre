package tech.kayys.erp.warehouse.infrastructure.persistence.mapper;

import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.model.Warehouse;
import tech.kayys.erp.warehouse.infrastructure.persistence.entity.WarehouseEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

/**
 * Mapper between Warehouse domain and persistence entities.
 */
@ApplicationScoped
public class WarehouseMapper {

    public WarehouseEntity toEntity(Warehouse warehouse) {
        WarehouseEntity entity = new WarehouseEntity();
        entity.id = warehouse.getId().getValue();
        entity.code = warehouse.getCode();
        entity.name = warehouse.getName();
        entity.description = warehouse.getDescription();
        entity.address = warehouse.getAddress();
        entity.city = warehouse.getCity();
        entity.state = warehouse.getState();
        entity.postalCode = warehouse.getPostalCode();
        entity.country = warehouse.getCountry();
        entity.phone = warehouse.getPhone();
        entity.email = warehouse.getEmail();
        entity.managerId = warehouse.getManagerId() != null ? 
            UUID.fromString(warehouse.getManagerId()) : null;
        entity.capacity = warehouse.getCapacity();
        entity.currentStockCount = warehouse.getCurrentStockCount();
        entity.active = warehouse.isActive();
        entity.defaultWarehouse = warehouse.isDefaultWarehouse();
        entity.notes = warehouse.getNotes();
        entity.zones = warehouse.getZones();
        entity.version = warehouse.getVersion();
        entity.createdAt = warehouse.getCreatedAt();
        entity.updatedAt = warehouse.getUpdatedAt();
        return entity;
    }

    public Warehouse toDomain(WarehouseEntity entity) {
        Warehouse warehouse = new Warehouse(WarehouseId.of(entity.id));
        warehouse.setCode(entity.code);
        warehouse.setName(entity.name);
        warehouse.setDescription(entity.description);
        warehouse.setAddress(entity.address);
        warehouse.setCity(entity.city);
        warehouse.setState(entity.state);
        warehouse.setPostalCode(entity.postalCode);
        warehouse.setCountry(entity.country);
        warehouse.setPhone(entity.phone);
        warehouse.setEmail(entity.email);
        warehouse.setManagerId(entity.managerId != null ? 
            entity.managerId.toString() : null);
        warehouse.setCapacity(entity.capacity);
        warehouse.setCurrentStockCount(entity.currentStockCount);
        warehouse.setActive(entity.active);
        warehouse.setDefaultWarehouse(entity.defaultWarehouse);
        warehouse.setNotes(entity.notes);
        warehouse.setZones(entity.zones);
        warehouse.setVersion(entity.version);
        warehouse.setCreatedAt(entity.createdAt);
        warehouse.setUpdatedAt(entity.updatedAt);
        return warehouse;
    }
}