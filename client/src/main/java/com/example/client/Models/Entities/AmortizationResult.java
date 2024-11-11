package com.example.client.Models.Entities;

public class AmortizationResult {
    private Long id;                            // Уникальный идентификатор результата амортизации
    private Long assetId;                       // Внешний ключ на основное средство
    private Long methodId;                      // Внешний ключ на метод амортизации
    private String assetName;                   // Название основного средства (ОС)
    private String inventoryNumber;             // Инвентарный номер ОС
    private String depreciationMethod;           // Метод амортизации
    private double initialCost;                  // Начальная стоимость
    private double initialDepreciation;          // Начальная амортизация
    private double increaseValue;                // Увеличение стоимости
    private double periodDepreciation;           // Амортизация за период
    private double decreaseValue;                // Уменьшение стоимости
    private double finalCost;                    // Конечная стоимость
    private double finalDepreciation;            // Конечная амортизация
    private double remainingValue;               // Остаточная стоимость

    // Конструктор, геттеры и сеттеры
}

