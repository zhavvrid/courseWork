package com.example.client.Models.Entities;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DepreciationCalculation {


    private Long id;

    private FixedAsset fixedAsset;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String calculationDate; // Дата расчета
    private BigDecimal depreciationAmount; // Сумма амортизации за период
    private BigDecimal accumulatedDepreciation; // Накопленная амортизация
    private BigDecimal residualValue; // Остаточная стоимость
    private String depreciationMethod; // Метод амортизации
    private BigDecimal initialCost; // Начальная стоимость актива

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
            // Try to parse the date to validate it
            LocalDate date = LocalDate.parse(calculationDate, DATE_FORMATTER);
            this.calculationDate = date.format(DATE_FORMATTER); // Store in the specified format
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
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

    public void setInitialCost(BigDecimal initialCost) {
        this.initialCost = initialCost;
    }

    // Метод для вычисления новых значений амортизации
    public void calculateNewValues(BigDecimal newDepreciationAmount) {
        this.accumulatedDepreciation = this.accumulatedDepreciation.add(newDepreciationAmount);
        this.residualValue = this.initialCost.subtract(this.accumulatedDepreciation);
    }
    public int getUsefulLife() {
        return fixedAsset.getUsefulLife();
    }
    public double getResidualValueFromAsset() {
        return fixedAsset.getResidualValue();
    }
    public String getPurchaseDate(){
        return fixedAsset.getPurchaseDate();
    }
}
