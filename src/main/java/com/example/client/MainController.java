package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.User;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button authSignInButton;

    @FXML
    private Label loginSignUpButton;

    @FXML
    private TextField login_field;

    @FXML
    private PasswordField password_field;

    @FXML
    void initialize() {
        loginSignUpButton.setOnMouseClicked(event -> openSignUpWindow());
    }

    private void openSignUpWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void SignUp_Pressed(MouseEvent event) {
        try {

            Stage stage = (Stage) ((javafx.scene.control.Label) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("signUp.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void authSignInPressed(ActionEvent actionEvent) {
        String login = login_field.getText();
        String password = password_field.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, введите логин и пароль.");
            return;
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        Request request = new Request();
        request.setRequestType(RequestType.LOGIN);
        request.setMessage(new Gson().toJson(user));

        String jsonRequest = new Gson().toJson(request);
        ClientSocket.getInstance().getOut().println(jsonRequest);
        ClientSocket.getInstance().getOut().flush();

        handleServerResponse();
    }

    private void handleServerResponse() {
        try {
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseLine = in.readLine();
            if (responseLine != null) {
                Response response = new Gson().fromJson(responseLine, Response.class);
                showAlert("Результат", response.getMessage());
                User authenticatedUser = response.getUser();
                CurrentUser.getInstance().setUser(authenticatedUser);
                if (response.getSuccess()) {
                    openMainAppWindow(response.getRole());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
        }
    }

    private void openMainAppWindow(String role) {
        try {
            Stage stage = (Stage) authSignInButton.getScene().getWindow();
            Parent root;
            if ("admin".equalsIgnoreCase(role)) {
                root = FXMLLoader.load(getClass().getResource("AdminMenu.fxml"));
            } else if ("accountant".equalsIgnoreCase(role)) {
                root = FXMLLoader.load(getClass().getResource("AccountantPanel.fxml"));
            } else {
                showAlert("Ошибка", "Неизвестная роль пользователя.");
                return;
            }
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Не удалось открыть главное окно приложения: " + e.getMessage());
            showAlert("Ошибка", "Не удалось открыть главное окно приложения: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
