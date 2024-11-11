package com.example.client.Models.Entities;

import java.math.BigDecimal;

public class ProductionBasedMethod {
    private Long assetId;

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

