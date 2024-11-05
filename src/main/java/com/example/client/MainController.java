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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

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
    private Button loginSignUpButton;

    @FXML
    private TextField login_field;

    @FXML
    private PasswordField password_field;

    @FXML
    void initialize() {
        loginSignUpButton.setOnAction(actionEvent -> openSignUpWindow());
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
    public void SignUp_Pressed(ActionEvent actionEvent) throws IOException{
        Stage stage = (Stage) loginSignUpButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("signUp.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
    @FXML
    public void authSignInPressed(ActionEvent actionEvent) {
        String login = login_field.getText();
        String password = password_field.getText();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Пожалуйста, введите логин и пароль.");
            return;
        }

        // Создаем пользователя для отправки на сервер
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);

        // Создаем запрос на авторизацию
        Request request = new Request();
        request.setRequestType(RequestType.LOGIN);
        request.setMessage(new Gson().toJson(user));

        // Отправляем запрос на сервер
        String jsonRequest = new Gson().toJson(request);
        ClientSocket.getInstance().getOut().println(jsonRequest);
        ClientSocket.getInstance().getOut().flush();

        // Обрабатываем ответ от сервера
        handleServerResponse();
    }

    private void handleServerResponse() {
        try {
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseLine = in.readLine();
            if (responseLine != null) {
                Response response = new Gson().fromJson(responseLine, Response.class);
                showAlert("Ответ от сервера", response.getMessage());
                // Если авторизация успешна, проверяем роль пользователя
                if (response.getSuccess()) {
                    openMainAppWindow(response.getRole()); // Pass the role to the method
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
                root = FXMLLoader.load(getClass().getResource("AccountantPanel.fxml")); // Add your accountant menu
            } else {
                showAlert("Ошибка", "Неизвестная роль пользователя.");
                return; // Exit if the role is unknown
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
