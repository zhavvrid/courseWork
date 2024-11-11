package com.example.server.Models.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ProductionBasedMethod")
public class ProductionBasedMethod {
    @Id
    private Long assetId;

    @OneToOne
    @JoinColumn(name = "asset_id")
    private FixedAsset asset;

    private BigDecimal totalExpectedProduction;

    public ProductionBasedMethod(Long assetId, FixedAsset asset, BigDecimal totalExpectedProduction) {
        this.assetId = assetId;
        this.asset = asset;
        this.totalExpectedProduction = totalExpectedProduction;
    }

    public ProductionBasedMethod() {

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

    public BigDecimal getTotalExpectedProduction() {
        return totalExpectedProduction;
    }

    public void setTotalExpectedProduction(BigDecimal totalExpectedProduction) {
        this.totalExpectedProduction = totalExpectedProduction;
    }
}

