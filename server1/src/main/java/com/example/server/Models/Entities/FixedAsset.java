package com.example.server.Models.Entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "fixed_asset")
public class FixedAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String inventoryNumber;

    private String category;

    @Column(nullable = false)
    private BigDecimal initialCost;

    private BigDecimal residualValue;

    private int usefulLife;

    private String purchaseDate; // Change LocalDate to String

    private String depreciationMethod;
    @OneToMany(mappedBy = "fixedAsset", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DepreciationCalculation> depreciationCalculations;


    public FixedAsset() {
    }

    public FixedAsset(int id, String name, String inventoryNumber, String category, BigDecimal initialCost,
                      BigDecimal residualValue, int usefulLife, String purchaseDate, String depreciationMethod) {
        this.id = id;
        this.name = name;
        this.inventoryNumber = inventoryNumber;
        this.category = category;
        this.initialCost = initialCost;
        this.residualValue = residualValue;
        this.usefulLife = usefulLife;
        setPurchaseDate(purchaseDate); // Use setter to ensure correct format
        this.depreciationMethod = depreciationMethod;
    }

    // Getters and Setters
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getInitialCost() {
        return initialCost;
    }

    public void setInitialCost(BigDecimal initialCost) {
        this.initialCost = initialCost;
    }

    public BigDecimal getResidualValue() {
        return residualValue;
    }

    public void setResidualValue(BigDecimal residualValue) {
        this.residualValue = residualValue;
    }

    public int getUsefulLife() {
        return usefulLife;
    }

    public void setUsefulLife(int usefulLife) {
        this.usefulLife = usefulLife;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate; // Assume input is already validated
    }

    public String getDepreciationMethod() {
        return depreciationMethod;
    }

    public void setDepreciationMethod(String depreciationMethod) {
        this.depreciationMethod = depreciationMethod;
    }

    @Override
    public String toString() {
        return "FixedAsset{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", inventoryNumber='" + inventoryNumber + '\'' +
                ", category='" + category + '\'' +
                ", initialCost=" + initialCost +
                ", residualValue=" + residualValue +
                ", usefulLife=" + usefulLife +
                ", purchaseDate='" + purchaseDate + '\'' + // String representation
                ", depreciationMethod='" + depreciationMethod + '\'' +
                '}';
    }
}
