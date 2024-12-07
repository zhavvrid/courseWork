package com.example.server.Models.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "acceleratedDepreciation")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("Accelerated")
public class AcceleratedDepreciation extends DepreciationCalculation {

    @Column(name = "depreciationRate", columnDefinition = "DECIMAL(15, 2) DEFAULT 0.15")
    private BigDecimal depreciationRate = BigDecimal.valueOf(0.15);

    public AcceleratedDepreciation() {}

    public AcceleratedDepreciation(Long id, FixedAsset fixedAsset, String calculationDate, BigDecimal depreciationAmount, BigDecimal accumulatedDepreciation, BigDecimal residualValue, String depreciationMethod, BigDecimal initialCost, BigDecimal depreciationRate) {
        super(id, fixedAsset, calculationDate, depreciationAmount, accumulatedDepreciation, residualValue, depreciationMethod, initialCost);
        this.depreciationRate = depreciationRate;
    }

    public BigDecimal getDepreciationRate() {
        return depreciationRate;
    }

    public void setDepreciationRate(BigDecimal depreciationRate) {
        this.depreciationRate = depreciationRate;
    }
}
