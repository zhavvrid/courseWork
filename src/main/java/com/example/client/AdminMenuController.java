package com.example.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMenuController {
    @FXML
    private Button headerButton;

    @FXML
    private Button manageAccountsButton;

    @FXML
    private Button manageAssetsButton;

    @FXML
    private Button depreciationReportsButton;

    @FXML
    private Button messageAccountantButton;

    @FXML
    private Button exitButton;
    @FXML
    void initialize() {
        manageAssetsButton.setOnAction(actionEvent -> openManageAssetWindow());
    }
    @FXML
    private void openManageAssetWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FixedAssetPanel.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть панель управления основными средствами.");
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
