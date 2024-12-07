package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.Role;
import com.example.client.Models.Entities.User;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
public class SignUpController {
    @FXML
    private TextField login_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private TextField name_field;
    @FXML
    private TextField email_field;
    @FXML
    private CheckBox adminCheckBox;
    @FXML
    private CheckBox accountantCheckBox;

    @FXML
    public void registerUser(ActionEvent actionEvent) {
        User user = new User();
        user.setLogin(login_field.getText());
        user.setPassword(password_field.getText());
        user.setEmail(email_field.getText());

        Role role = null;
        if (adminCheckBox.isSelected()) {
            role = new Role(1, "admin");
        } else if (accountantCheckBox.isSelected()) {
            role = new Role(2, "accountant");
        } else {
            showAlert("Ошибка", "Пожалуйста, выберите хотя бы одну роль.");
            return;
        }
        user.setRole(role);


        Request request = new Request();
        request.setRequestType(RequestType.REGISTER);
        request.setMessage(new Gson().toJson(user));

        String jsonRequest = new Gson().toJson(request);
        ClientSocket.getInstance().getOut().println(jsonRequest);
        ClientSocket.getInstance().getOut().flush();

        handleServerResponse();
        System.out.println("Отправляемое сообщение: " + jsonRequest);
    }

    private void handleServerResponse() {
        try {
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseLine = in.readLine();
            if (responseLine != null) {
                Response response = new Gson().fromJson(responseLine, Response.class);
                showAlert("Результат", response.getMessage());
                closeWindow();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
        }
    }
    private void closeWindow() {
        Stage stage = (Stage) login_field.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        login_field.clear();
        password_field.clear();
        login_field.clear();
        name_field.clear();
        email_field.clear();
    }
    @FXML
    private void handleClearButtonAction() {
        clearFields();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
