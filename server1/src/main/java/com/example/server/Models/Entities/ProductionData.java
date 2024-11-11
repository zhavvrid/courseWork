package com.example.server.Models.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ProductionData")
public class ProductionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private FixedAsset asset;

    private LocalDate productionDate;
    private BigDecimal unitsProduced;

    public ProductionData(Long id, FixedAsset asset, LocalDate productionDate, BigDecimal unitsProduced) {
        this.id = id;
        this.asset = asset;
        this.productionDate = productionDate;
        this.unitsProduced = unitsProduced;
    }

    public ProductionData() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FixedAsset getAsset() {
        return asset;
    }

    public void setAsset(FixedAsset asset) {
        this.asset = asset;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public BigDecimal getUnitsProduced() {
        return unitsProduced;
    }

    public void setUnitsProduced(BigDecimal unitsProduced) {
        this.unitsProduced = unitsProduced;
    }
}

