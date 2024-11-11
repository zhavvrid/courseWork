package com.example.server.Models.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Entity
@Table(name = "declining_balance_depreciation")
public class DecliningBalanceDepreciation extends DepreciationCalculation {

    @Column(name = "depreciation_rate", columnDefinition = "DECIMAL(15, 2) DEFAULT 0.2")
    private BigDecimal depreciationRate = BigDecimal.valueOf(0.2);

    public DecliningBalanceDepreciation() {}

    // Additional methods if needed
}
