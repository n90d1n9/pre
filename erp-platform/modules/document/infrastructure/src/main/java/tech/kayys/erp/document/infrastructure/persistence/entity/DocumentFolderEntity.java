package tech.kayys.erp.document.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Document folder entity for persistence.
 */
@Entity
@Table(name = "document_folders", indexes = {
    @Index(name = "idx_folder_name", columnList = "name"),
    @Index(name = "idx_folder_parent", columnList = "parent_folder_id"),
    @Index(name = "idx_folder_owner", columnList = "owner_id")
})
public class DocumentFolderEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @Column(name = "description", length = 2000)
    public String description;

    @Column(name = "parent_folder_id", columnDefinition = "UUID")
    public UUID parentFolderId;

    @Column(name = "owner_id", columnDefinition = "UUID")
    public UUID ownerId;

    @Column(name = "owner_name", length = 100)
    public String ownerName;

    @Column(name = "department", length = 100)
    public String department;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "folder_permissions", joinColumns = @JoinColumn(name = "folder_id"))
    @Column(name = "permission", length = 50)
    public List<String> permissions = new ArrayList<>();
}