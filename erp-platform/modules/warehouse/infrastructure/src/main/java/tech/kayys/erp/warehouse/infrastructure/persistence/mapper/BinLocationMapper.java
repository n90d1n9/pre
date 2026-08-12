package tech.kayys.erp.warehouse.infrastructure.persistence.mapper;

import tech.kayys.erp.warehouse.domain.identifier.BinLocationId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.model.BinLocation;
import tech.kayys.erp.warehouse.infrastructure.persistence.entity.BinLocationEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper between BinLocation domain and persistence entities.
 */
@ApplicationScoped
public class BinLocationMapper {

    public BinLocationEntity toEntity(BinLocation bin) {
        BinLocationEntity entity = new BinLocationEntity();
        entity.id = bin.getId().getValue();
        entity.warehouseId = bin.getWarehouseId().getValue();
        entity.code = bin.getCode();
        entity.name = bin.getName();
        entity.description = bin.getDescription();
        entity.binType = bin.getBinType();
        entity.status = bin.getStatus();
        entity.zone = bin.getZone();
        entity.aisle = bin.getAisle();
        entity.level = bin.getLevel();
        entity.position = bin.getPosition();
        entity.capacity = bin.getCapacity();
        entity.occupied = bin.getOccupied();
        entity.maxWeight = bin.getMaxWeight();
        entity.maxLength = bin.getMaxLength();
        entity.maxWidth = bin.getMaxWidth();
        entity.maxHeight = bin.getMaxHeight();
        entity.active = bin.isActive();
        entity.notes = bin.getNotes();
        if (bin.getAssignedProductIds() != null) {
            entity.assignedProductIds = bin.getAssignedProductIds().stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        }
        entity.version = bin.getVersion();
        entity.createdAt = bin.getCreatedAt();
        entity.updatedAt = bin.getUpdatedAt();
        return entity;
    }

    public BinLocation toDomain(BinLocationEntity entity) {
        BinLocation bin = new BinLocation(BinLocationId.of(entity.id));
        bin.setWarehouseId(WarehouseId.of(entity.warehouseId));
        bin.setCode(entity.code);
        bin.setName(entity.name);
        bin.setDescription(entity.description);
        bin.setBinType(entity.binType);
        bin.setStatus(entity.status);
        bin.setZone(entity.zone);
        bin.setAisle(entity.aisle);
        bin.setLevel(entity.level);
        bin.setPosition(entity.position);
        bin.setCapacity(entity.capacity);
        bin.setOccupied(entity.occupied);
        bin.setMaxWeight(entity.maxWeight);
        bin.setMaxDimensions(entity.maxLength, entity.maxWidth, entity.maxHeight);
        bin.setActive(entity.active);
        bin.setNotes(entity.notes);
        if (entity.assignedProductIds != null) {
            bin.setAssignedProductIds(entity.assignedProductIds.stream()
                .map(UUID::toString)
                .collect(Collectors.toList()));
        }
        bin.setVersion(entity.version);
        bin.setCreatedAt(entity.createdAt);
        bin.setUpdatedAt(entity.updatedAt);
        return bin;
    }
}