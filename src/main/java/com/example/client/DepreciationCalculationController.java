package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DepreciationCalculationController {

    @FXML
    private VBox inputFieldsContainer;

    private String depreciationMethod;

    public void setDepreciationMethod(String method) {
        this.depreciationMethod = method;
        setupInputFields();
    }

    private void setupInputFields() {
        inputFieldsContainer.getChildren().clear();

        switch (depreciationMethod) {
            case "Производственный":
                TextField totalProductionField = new TextField();
                totalProductionField.setPromptText("Общее производство");
                inputFieldsContainer.getChildren().add(totalProductionField);
                break;

            case "Линейный":
                TextField usefulLifeField = new TextField();
                usefulLifeField.setPromptText("Срок полезного использования");
                inputFieldsContainer.getChildren().add(usefulLifeField);
                break;

            // Добавьте другие методы амортизации, если необходимо

            default:
                showAlert("Ошибка", "Метод амортизации не распознан.");
                break;
        }
    }

    @FXML
    private void calculateDepreciation() {
        // Логика расчета амортизации. Здесь вы можете использовать значения полей ввода
        showAlert("Рассчитать амортизацию", "Расчет успешно выполнен!");
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) inputFieldsContainer.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
