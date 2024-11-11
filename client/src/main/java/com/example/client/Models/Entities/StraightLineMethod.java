package com.example.client.Models.Entities;

import java.math.BigDecimal;

public class StraightLineMethod {
    private Long assetId;

    private FixedAsset asset;

    private BigDecimal annualDepreciation;

    public StraightLineMethod(Long assetId, FixedAsset asset, BigDecimal annualDepreciation) {
        this.assetId = assetId;
        this.asset = asset;
        this.annualDepreciation = annualDepreciation;
    }

    public StraightLineMethod() {

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

    public BigDecimal getAnnualDepreciation() {
        return annualDepreciation;
    }

    public void setAnnualDepreciation(BigDecimal annualDepreciation) {
        this.annualDepreciation = annualDepreciation;
    }
}

