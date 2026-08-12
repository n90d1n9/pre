package tech.kayys.erp.document.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.document.domain.identifier.FolderId;
import tech.kayys.erp.document.domain.model.DocumentFolder;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for DocumentFolder aggregates.
 */
public interface DocumentFolderRepository extends Repository<DocumentFolder, FolderId> {

    /**
     * Finds subfolders of a parent folder.
     */
    CompletionStage<List<DocumentFolder>> findSubfolders(FolderId parentId);

    /**
     * Finds root folders.
     */
    CompletionStage<List<DocumentFolder>> findRootFolders();

    /**
     * Finds folders by owner.
     */
    CompletionStage<List<DocumentFolder>> findByOwner(String ownerId);

    /**
     * Finds folders by department.
     */
    CompletionStage<List<DocumentFolder>> findByDepartment(String department);

    /**
     * Finds folders by name containing text.
     */
    CompletionStage<List<DocumentFolder>> findByNameContaining(String name);

    /**
     * Gets the folder path.
     */
    CompletionStage<List<DocumentFolder>> getFolderPath(FolderId folderId);

    /**
     * Checks if a folder name exists in a parent.
     */
    CompletionStage<Boolean> existsByNameAndParent(String name, FolderId parentId);
}