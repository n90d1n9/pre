package tech.kayys.erp.integration.infrastructure.persistence.repository;

import tech.kayys.erp.integration.infrastructure.persistence.entity.IntegrationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Integration entities.
 */
@ApplicationScoped
public class IntegrationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(IntegrationEntity entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public Optional<IntegrationEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(IntegrationEntity.class, id));
    }

    public Optional<IntegrationEntity> findByCode(String code) {
        List<IntegrationEntity> results = entityManager.createQuery(
            "SELECT e FROM IntegrationEntity e WHERE e.code = :code", 
            IntegrationEntity.class)
            .setParameter("code", code)
            .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<IntegrationEntity> findAll() {
        return entityManager.createQuery(
            "SELECT e FROM IntegrationEntity e ORDER BY e.name", 
            IntegrationEntity.class).getResultList();
    }

    public List<IntegrationEntity> findByStatus(String status) {
        return entityManager.createQuery(
            "SELECT e FROM IntegrationEntity e WHERE e.status = :status ORDER BY e.name", 
            IntegrationEntity.class)
            .setParameter("status", status)
            .getResultList();
    }

    @Transactional
    public void delete(UUID id) {
        IntegrationEntity entity = entityManager.find(IntegrationEntity.class, id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
