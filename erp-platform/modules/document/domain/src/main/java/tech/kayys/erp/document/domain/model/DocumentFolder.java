package tech.kayys.erp.document.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.document.domain.identifier.FolderId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Document folder aggregate root.
 * Represents a folder for organizing documents.
 */
public final class DocumentFolder extends AggregateRoot<FolderId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private FolderId parentFolderId;
    private List<FolderId> subFolders;
    private List<String> permissions;
    private String ownerId;
    private String ownerName;
    private String department;
    private boolean active;
    private String notes;

    private DocumentFolder(FolderId id) {
        super(id);
        this.subFolders = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.active = true;
    }

    private DocumentFolder() {
        super();
    }

    /**
     * Factory method to create a new folder.
     */
    public static DocumentFolder create(
            FolderId id,
            String name,
            String ownerId,
            String ownerName,
            String department) {
        DocumentFolder folder = new DocumentFolder(id);
        folder.name = name;
        folder.ownerId = ownerId;
        folder.ownerName = ownerName;
        folder.department = department;
        return folder;
    }

    /**
     * Adds a subfolder.
     */
    public void addSubFolder(FolderId subFolderId) {
        if (!subFolders.contains(subFolderId)) {
            subFolders.add(subFolderId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a subfolder.
     */
    public void removeSubFolder(FolderId subFolderId) {
        subFolders.remove(subFolderId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the parent folder.
     */
    public void setParentFolder(FolderId parentFolderId) {
        if (this.id.equals(parentFolderId)) {
            throw new IllegalArgumentException("Cannot set self as parent");
        }
        this.parentFolderId = parentFolderId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a permission to the folder.
     */
    public void addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a permission from the folder.
     */
    public void removePermission(String permission) {
        permissions.remove(permission);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the folder.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the folder.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the folder information.
     */
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the number of subfolders.
     */
    public int getSubFolderCount() {
        return subFolders.size();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public FolderId getParentFolderId() { return parentFolderId; }
    public List<FolderId> getSubFolders() { return Collections.unmodifiableList(subFolders); }
    public List<String> getPermissions() { return Collections.unmodifiableList(permissions); }
    public String getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getDepartment() { return department; }
    public boolean isActive() { return active; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDepartment(String department) {
        this.department = department;
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
        return "DocumentFolder{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", subFolders=" + subFolders.size() +
                ", active=" + active +
                '}';
    }
}