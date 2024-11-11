package com.example.server.Models.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Entity
@Table(name = "accelerated_depreciation")
public class AcceleratedDepreciation extends DepreciationCalculation {

    @Column(name = "depreciation_rate", columnDefinition = "DECIMAL(15, 2) DEFAULT 0.15")
    private BigDecimal depreciationRate = BigDecimal.valueOf(0.15);

    public AcceleratedDepreciation() {}

}
