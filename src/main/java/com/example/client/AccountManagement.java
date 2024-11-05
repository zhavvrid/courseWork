package com.example.client;

import com.example.client.Models.Entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AccountManagement {

    @FXML
    private TableView<User> accountTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> loginColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;

    // Метод для добавления новой учетной записи
    @FXML
    private void handleAddAccount() {
        // Логика добавления учетной записи
    }

    // Метод для редактирования выбранной учетной записи
    @FXML
    private void handleEditAccount() {
        // Логика редактирования учетной записи
    }

    // Метод для удаления выбранной учетной записи
    @FXML
    private void handleDeleteAccount() {
        // Логика удаления учетной записи
    }

    // Метод для очистки полей ввода
    @FXML
    private void handleClearFields() {
        usernameField.clear();
        loginField.clear();
        passwordField.clear();
        emailField.clear();
    }
}
