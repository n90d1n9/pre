package tech.kayys.syirkah.asset.infrastructure.persistence.inspection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import tech.kayys.syirkah.asset.domain.inspection.AssetCondition;
import tech.kayys.syirkah.asset.domain.inspection.InspectionResult;
import tech.kayys.syirkah.foundation.persistence.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "asset_inspection_item")
public class AssetInspectionItemEntity extends BaseEntity {

    @Column(name = "tenant_id", nullable = false, length = 100)
    public String tenantId;

    @Column(name = "inspection_id", nullable = false)
    public UUID inspectionId;

    @Column(name = "component", nullable = false, length = 255)
    public String component;

    @Column(name = "description", length = 1000)
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", length = 50)
    public AssetCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", length = 50)
    public InspectionResult result;

    @Column(name = "notes", columnDefinition = "text")
    public String notes;
}
