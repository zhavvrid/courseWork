package com.example.client.Models.Entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FixedAsset {
    private int id;
    private String name;
    private String inventoryNumber;
    private String purchaseDate; // Change LocalDate to String
    private double initialCost; // первоначальная стоимость
    private int usefulLife; // СПИ
    private double residualValue; // остаточная стоимость
    private String depreciationMethod;
    private String category;

    // DateTimeFormatter to define the date format
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FixedAsset(int id, String name, String inventoryNumber, String purchaseDate, double initialCost, int usefulLife, double residualValue, String depreciationMethod, String category) {
        this.id = id;
        this.name = name;
        this.inventoryNumber = inventoryNumber;
        setPurchaseDate(purchaseDate); // Use setter to enforce the format
        this.initialCost = initialCost;
        this.usefulLife = usefulLife;
        this.residualValue = residualValue;
        this.depreciationMethod = depreciationMethod;
        this.category = category;
    }

    public FixedAsset(String name, String inventoryNumber, String purchaseDate, double initialCost, int usefulLife, double residualValue, String depreciationMethod, String category) {
        this.name = name;
        this.inventoryNumber = inventoryNumber;
        this.purchaseDate = purchaseDate;
        this.initialCost = initialCost;
        this.usefulLife = usefulLife;
        this.residualValue = residualValue;
        this.depreciationMethod = depreciationMethod;
        this.category = category;
    }

    @Override
    public String toString() {
        return "FixedAsset{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", inventoryNumber='" + inventoryNumber + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' + // String representation
                ", initialCost=" + initialCost +
                ", usefulLife=" + usefulLife +
                ", residualValue=" + residualValue +
                ", depreciationMethod='" + depreciationMethod + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    // Setter to ensure the date is in the correct format
    public void setPurchaseDate(String purchaseDate) {
        try {
            // Try to parse the date to validate it
            LocalDate date = LocalDate.parse(purchaseDate, DATE_FORMATTER);
            this.purchaseDate = date.format(DATE_FORMATTER); // Store in the specified format
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    public double getInitialCost() {
        return initialCost;
    }

    public void setInitialCost(double initialCost) {
        this.initialCost = initialCost;
    }

    public int getUsefulLife() {
        return usefulLife;
    }

    public void setUsefulLife(int usefulLife) {
        this.usefulLife = usefulLife;
    }

    public double getResidualValue() {
        return residualValue;
    }

    public void setResidualValue(double residualValue) {
        this.residualValue = residualValue;
    }

    public String getDepreciationMethod() {
        return depreciationMethod;
    }

    public void setDepreciationMethod(String depreciationMethod) {
        this.depreciationMethod = depreciationMethod;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Additional method to convert purchaseDate to LocalDate
    public LocalDate getPurchaseDateAsLocalDate() {
        return LocalDate.parse(purchaseDate, DATE_FORMATTER);
    }
}
