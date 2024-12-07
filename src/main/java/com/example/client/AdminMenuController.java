package com.example.client;

import com.example.client.Utility.WindowUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import java.io.IOException;

public class AdminMenuController {
    @FXML
    private Button headerButton;

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
            Stage stage = (Stage) manageAssetsButton.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Таблица основных средств");
            stage.show();

            WindowUtils.centerWindow(stage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть панель управления основными средствами.");
        }
    }


    @FXML
    private void openManageAccountWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) manageAssetsButton.getScene().getWindow();
            stage.setTitle("Управление учетными записями");
            stage.setScene(new Scene(root));
            stage.show();
            WindowUtils.centerWindow(stage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть панель управления основными средствами.");
        }
    }

    @FXML
    private void sendMessage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SendMessage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) messageAccountantButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            WindowUtils.centerWindow(stage);
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

    public void openReportsWindow(ActionEvent actionEvent) {


    }

    public void exitApplication(ActionEvent actionEvent) {
    }
}
