package tech.kayys.syirkah.inventory.domain.inventory;

import java.util.Objects;

/** Physical storage warehouse facility. */
public record Warehouse(WarehouseId id, String code, String name, String location) {
    public Warehouse {
        Objects.requireNonNull(id);
        Objects.requireNonNull(code);
        Objects.requireNonNull(name);
    }
}
