package tech.kayys.erp.security.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.security.domain.identifier.PermissionId;

import java.time.Instant;

/**
 * Permission aggregate root.
 * Defines a specific permission that can be granted to users/roles.
 */
public final class Permission extends AggregateRoot<PermissionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private String resource;
    private String action; // CREATE, READ, UPDATE, DELETE, EXECUTE
    private String scope; // GLOBAL, TENANT, COMPANY, USER
    private boolean active;
    private String notes;

    private Permission(PermissionId id) {
        super(id);
        this.active = true;
    }

    private Permission() {
        super();
    }

    /**
     * Factory method to create a new permission.
     */
    public static Permission create(
            PermissionId id,
            String code,
            String name,
            String resource,
            String action,
            String scope) {
        Permission permission = new Permission(id);
        permission.code = code;
        permission.name = name;
        permission.resource = resource;
        permission.action = action;
        permission.scope = scope;
        return permission;
    }

    /**
     * Activates the permission.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the permission.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getScope() { return scope; }
    public boolean isActive() { return active; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}