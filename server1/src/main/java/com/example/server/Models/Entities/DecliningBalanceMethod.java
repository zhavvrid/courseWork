package com.example.server.Models.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "DecliningBalanceMethod")
public class DecliningBalanceMethod {
    @Id
    private Long assetId;

    @OneToOne
    @JoinColumn(name = "asset_id")
    private FixedAsset asset;

    private BigDecimal depreciationRate;  // Ставка амортизации в долях (например, 0.20 для 20%)

    public DecliningBalanceMethod(Long assetId, FixedAsset asset, BigDecimal depreciationRate) {
        this.assetId = assetId;
        this.asset = asset;
        this.depreciationRate = depreciationRate;
    }

    public DecliningBalanceMethod() {

    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public FixedAsset getAsset() {
        return asset;
    }

    public void setAsset(FixedAsset asset) {
        this.asset = asset;
    }

    public BigDecimal getDepreciationRate() {
        return depreciationRate;
    }

    public void setDepreciationRate(BigDecimal depreciationRate) {
        this.depreciationRate = depreciationRate;
    }
}

