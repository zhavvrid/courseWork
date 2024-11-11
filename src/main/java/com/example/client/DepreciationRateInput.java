package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class DepreciationRateInput {
    @FXML
    private TextField depreciationRateField;

    private CalculateAmortizationController parentController;

    public void setParentController(CalculateAmortizationController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleConfirm() {
        try {
            BigDecimal depreciationRate = new BigDecimal(depreciationRateField.getText());
            if (depreciationRate.compareTo(BigDecimal.ZERO) <= 0 || depreciationRate.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("Процент должен быть в пределах от 0 до 1.");
            }
            parentController.setDepreciationRate(depreciationRate);
        } catch (Exception e) {
            showAlert("Ошибка", "Неверный формат процента. Введите значение от 0 до 1.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
