/*
package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.Message;
import com.example.client.Models.Entities.MessageIterator;
import com.example.client.Models.Entities.User;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.google.gson.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MessageController {

    @FXML
    private TextArea messageInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button backButton;
    @FXML
    private TableView<User> accountTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, String> loginColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private ListView<String> messageListView;

    private User currentUser;
    private User chatPartner;

    Gson gson = new Gson();

    @FXML
    private void initialize() {
        // Инициализация таблицы с пользователями
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().getName())
        );
        loadUsers();
    }

    @FXML
    private void handleMarkAsRead(*/
/*//*
) {
        // Получение выбранного сообщения
        Message selectedMessage = messagesTable.getSelectionModel().getSelectedItem();

        if (selectedMessage == null) {
            showAlert("Ошибка", "Выберите сообщение для изменения статуса.");
            return;
        }

        // Проверяем, что сообщение уже не прочитано
        if (selectedMessage.isRead()) {
            showAlert("Информация", "Сообщение уже помечено как прочитанное.");
            return;
        }

        // Устанавливаем статус на "прочитано"
        selectedMessage.setRead(true);

        // Отправляем запрос на сервер
        Request request = new Request();
        request.setRequestType(RequestType.UPDATE_MESSAGE_STATUS);
        request.setMessage(new Gson().toJson(selectedMessage));

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(new Gson().toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseLine = in.readLine();
            Response response = new Gson().fromJson(responseLine, Response.class);

            if (response.getSuccess()) {
                showAlert("Успех", "Сообщение помечено как прочитанное.");
                messagesTable.refresh(); // Обновляем отображение таблицы
            } else {
                showAlert("Ошибка", response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось обновить статус сообщения: " + e.getMessage());
        }
    }


    public void loadUsers() {
        Request request = new Request();
        request.setRequestType(RequestType.GETALL_USERS);
        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();
            System.out.println("Received response string: " + responseString); // Логируем полученный ответ

            if (responseString != null) { // Проверяем, что ответ не null
                Response response = gson.fromJson(responseString, Response.class);
                if (response.getSuccess()) {
                    String message = response.getMessage();
                    try {
                        List<User> users = gson.fromJson(message, new TypeToken<List<User>>() {}.getType());
                        Platform.runLater(() -> accountTable.getItems().setAll(users));
                    } catch (JsonSyntaxException e) {
                        Platform.runLater(() -> showAlert("Ошибка", "Неправильный формат данных: " + message));
                    }
                } else {
                    Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
                }
            } else {
                showAlert("Ошибка", "Сервер вернул пустой ответ.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить пользователей: " + e.getMessage());
        }
    }

    public void loadMessages(User selectedUser) {
        User currentUser = CurrentUser.getInstance().getUser();

        // Создаем запрос
        Request request = new Request();
        request.setRequestType(RequestType.GET_MESSAGES);  // Указываем тип запроса
        request.setMessage(new Gson().toJson(currentUser));  // Отправляем текущего пользователя в запросе

        // Отправляем запрос на сервер
        PrintWriter out = ClientSocket.getInstance().getOut();
        out.println(gson.toJson(request));
        out.flush();

        // Обрабатываем ответ (см. шаг 2)
        handleServerResponse();
    }

    private void handleServerResponse() {
        try {
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseLine = in.readLine();

            if (responseLine != null) {
                Response response = new Gson().fromJson(responseLine, Response.class); // Преобразуем ответ в объект

                if (response.getSuccess()) {
                    List<Message> messages = new Gson().fromJson(response.getMessage(), new TypeToken<List<Message>>(){}.getType());
                    displayMessages(messages);  // Отображаем сообщения в интерфейсе
                } else {
                    showAlert("Ошибка", "Не удалось получить сообщения.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
        }
    }

    private void displayMessages(List<Message> messages) {
        ObservableList<String> messageItems = FXCollections.observableArrayList();

        // Использование итератора для обхода сообщений
        MessageIterator iterator = new MessageIterator(messages);
        while (iterator.hasNext()) {
            Message message = iterator.next();
            String messageText = message.getSender().getLogin() + ": " + message.getContent();
            messageItems.add(messageText);
        }

        messageListView.setItems(messageItems);  // Загрузка сообщений в ListView
    }

    public void handleSendMessage() {
        User selectedUser = accountTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Ошибка", "Выберите получателя сообщения.");
            return;
        }

        User currentUser = CurrentUser.getInstance().getUser();
        String content = messageInput.getText().trim();
        if (content.isEmpty()) {
            showAlert("Ошибка", "Введите текст сообщения.");
            return;
        }

        Message message = new Message();
        message.setSender(currentUser);
        message.setReceiver(selectedUser);
        message.setContent(content);

        // Создание JSON объектов для sender и receiver
        JsonObject senderJson = new JsonObject();
        senderJson.addProperty("id", currentUser.getId());
        senderJson.addProperty("login", currentUser.getLogin());
        senderJson.addProperty("email", currentUser.getEmail());

        JsonObject receiverJson = new JsonObject();
        receiverJson.addProperty("id", selectedUser.getId());
        receiverJson.addProperty("login", selectedUser.getLogin());
        receiverJson.addProperty("email", selectedUser.getEmail());

        // Преобразование сообщения в JSON
        JsonObject messageJson = new JsonObject();
        messageJson.add("sender", senderJson);
        messageJson.add("receiver", receiverJson);
        messageJson.addProperty("content", message.getContent());
        messageJson.addProperty("isRead", message.isRead());
        System.out.println("string: " + messageJson); // Логируем полученный ответ

        // Отправка запроса
        Request request = new Request();
        request.setRequestType(RequestType.SEND_MESSAGE);
        request.setMessage(messageJson.toString());
        PrintWriter out = ClientSocket.getInstance().getOut();
        out.println(gson.toJson(request));
        out.flush();

        messageInput.clear();
        showAlert("Успех", "Сообщение отправлено.");
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminMenu.fxml"));
            Stage previousStage = new Stage();
            previousStage.setScene(new Scene(root));
            previousStage.setTitle("Меню администратора");
            previousStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть предыдущее меню: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

*/
