package tech.kayys.erp.warehouse.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;

import java.util.List;

/**
 * Command to create a new warehouse.
 */
public record CreateWarehouseCommand(
        WarehouseId warehouseId,
        String code,
        String name,
        String description,
        String address,
        String city,
        String state,
        String postalCode,
        String country,
        String phone,
        String email,
        String managerId,
        Integer capacity,
        List<String> zones,
        String notes
) implements Command<WarehouseId> {

    public CreateWarehouseCommand {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WarehouseId warehouseId;
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
        private Integer capacity;
        private List<String> zones;
        private String notes;

        public Builder warehouseId(WarehouseId warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder managerId(String managerId) {
            this.managerId = managerId;
            return this;
        }

        public Builder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder zones(List<String> zones) {
            this.zones = zones;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public CreateWarehouseCommand build() {
            if (warehouseId == null) {
                warehouseId = WarehouseId.generate();
            }
            return new CreateWarehouseCommand(
                warehouseId, code, name, description, address,
                city, state, postalCode, country, phone,
                email, managerId, capacity, zones, notes
            );
        }
    }
}