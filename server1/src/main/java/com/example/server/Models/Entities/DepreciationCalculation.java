package com.example.server.Models.Entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "depreciationcalculation")
public class DepreciationCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private FixedAsset fixedAsset;

    private String calculationDate; // Дата расчета
    private BigDecimal depreciationAmount; // Сумма амортизации за период
    private BigDecimal accumulatedDepreciation; // Накопленная амортизация
    private BigDecimal residualValue; // Остаточная стоимость
    @Column(name = "depreciation_method")
    private String depreciationMethod; // Метод амортизации
    @Column(name = "initial_cost")
    private BigDecimal initialCost; // Начальная стоимость актива
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DepreciationCalculation(Long id, FixedAsset fixedAsset, String calculationDate,
                                   BigDecimal depreciationAmount, BigDecimal accumulatedDepreciation,
                                   BigDecimal residualValue, String depreciationMethod,
                                   BigDecimal initialCost) {
        this.id = id;
        this.fixedAsset = fixedAsset;
        setCalculationDate(calculationDate);
        this.depreciationAmount = depreciationAmount;
        this.accumulatedDepreciation = accumulatedDepreciation;
        this.residualValue = residualValue;
        this.depreciationMethod = depreciationMethod;
        this.initialCost = initialCost;
    }

    public DepreciationCalculation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FixedAsset getFixedAsset() {
        return fixedAsset;
    }

    public void setFixedAsset(FixedAsset fixedAsset) {
        this.fixedAsset = fixedAsset;
    }

    public String getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(String calculationDate) {
        try {
            LocalDate date = LocalDate.parse(calculationDate, DATE_FORMATTER);
            this.calculationDate = date.format(DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты. Используйте yyyy-MM-dd.");
        }
    }

    public BigDecimal getDepreciationAmount() {
        return depreciationAmount;
    }

    public void setDepreciationAmount(BigDecimal depreciationAmount) {
        this.depreciationAmount = depreciationAmount;
    }

    public BigDecimal getAccumulatedDepreciation() {
        return accumulatedDepreciation;
    }

    public void setAccumulatedDepreciation(BigDecimal accumulatedDepreciation) {
        this.accumulatedDepreciation = accumulatedDepreciation;
    }

    public BigDecimal getResidualValue() {
        return residualValue;
    }

    public void setResidualValue(BigDecimal residualValue) {
        this.residualValue = residualValue;
    }

    public String getDepreciationMethod() {
        return depreciationMethod;
    }

    public void setDepreciationMethod(String depreciationMethod) {
        this.depreciationMethod = depreciationMethod;
    }

    public BigDecimal getInitialCost() {
        return initialCost;
    }

    public String getPurchaseDate(){
        return fixedAsset.getPurchaseDate();
    }
    public void setInitialCost(BigDecimal initialCost) {
        this.initialCost = initialCost;
    }

    // Метод для вычисления новых значений амортизации
    public void calculateNewValues(BigDecimal newDepreciationAmount) {
        this.accumulatedDepreciation = this.accumulatedDepreciation.add(newDepreciationAmount);
        this.residualValue = this.initialCost.subtract(this.accumulatedDepreciation);
    }
}
