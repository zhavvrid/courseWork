package com.example.server.Models.Entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "straightLineDepreciation")  // Указываем имя таблицы
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("StraightLine")  // Указываем тип амортизации
public class StraightLineDepreciation extends DepreciationCalculation {

    // Поле для annualDepreciation
    @Column(name = "annualDepreciation")  // Название колонки в базе данных
    private BigDecimal annualDepreciation;

    // Конструктор без параметров
    public StraightLineDepreciation() {}

    // Конструктор с параметрами
    public StraightLineDepreciation(Long id, FixedAsset fixedAsset, String calculationDate, BigDecimal depreciationAmount,
                                    BigDecimal accumulatedDepreciation, BigDecimal residualValue, String depreciationMethod,
                                    BigDecimal initialCost, BigDecimal annualDepreciation) {
        super(id, fixedAsset, calculationDate, depreciationAmount, accumulatedDepreciation, residualValue, depreciationMethod, initialCost);
        this.annualDepreciation = annualDepreciation;
    }

    // Геттер и сеттер для annualDepreciation
    public BigDecimal getAnnualDepreciation() {
        return annualDepreciation;
    }

    public void setAnnualDepreciation(BigDecimal annualDepreciation) {
        this.annualDepreciation = annualDepreciation;
    }
}
