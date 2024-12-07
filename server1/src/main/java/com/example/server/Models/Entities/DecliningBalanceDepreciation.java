package com.example.server.Models.Entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DecliningBalance")
@Table(name = "decliningBalanceDepreciation")
public class DecliningBalanceDepreciation extends DepreciationCalculation {

    @Column(name = "depreciationRate", columnDefinition = "DECIMAL(15, 2) DEFAULT 0.2")
    private BigDecimal depreciationRate = BigDecimal.valueOf(0.2);

    public DecliningBalanceDepreciation() {}

    public DecliningBalanceDepreciation(Long id, FixedAsset fixedAsset, String calculationDate, BigDecimal depreciationAmount, BigDecimal accumulatedDepreciation, BigDecimal residualValue, String depreciationMethod, BigDecimal initialCost, BigDecimal depreciationRate) {
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
