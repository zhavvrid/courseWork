package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.Message;
import com.example.client.Models.Entities.User;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class SendMessageController {

    @FXML
    private TableView<User> accountTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> loginColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TextArea messageInput;
    @FXML
    private Button sendMessageButton;
    @FXML
    private Button backButton;
    @FXML
    private ListView<Message> sentMessagesListView;
    @FXML
    private ListView<Message> receivedMessagesListView;

    private final Gson gson = new Gson();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().getName())
        );

        configureMessageListViews();

        // Загрузка пользователей в таблицу
        loadUsers();

        // Слушатель для выбора пользователя
        accountTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadMessages(newValue);
            }
        });
    }

    private void configureMessageListViews() {
        configureListView(receivedMessagesListView);
        configureListView(sentMessagesListView);
    }

    private void configureListView(ListView<Message> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    setText(message.getContent());
                }
            }
        });
    }

    private void loadUsers() {
        Request request = new Request();
        request.setRequestType(RequestType.GETALL_USERS);

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();

            if (responseString != null) {
                Response response = gson.fromJson(responseString, Response.class);
                if (response.getSuccess()) {
                    User currentUser = CurrentUser.getInstance().getUser();

                    List<User> users = gson.fromJson(response.getMessage(), new TypeToken<List<User>>() {}.getType());

                    List<User> filteredUsers = users.stream()
                            .filter(user -> user.getId() != currentUser.getId())
                            .collect(Collectors.toList());

                    // Обновление TableView
                    Platform.runLater(() -> accountTable.getItems().setAll(filteredUsers));
                } else {
                    showAlert("Ошибка", response.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить пользователей: " + e.getMessage());
        }
    }

    private void loadMessages(User selectedUser) {
        Request request = new Request();
        request.setRequestType(RequestType.GET_MESSAGES);
        request.setMessage(gson.toJson(selectedUser));

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();

            if (responseString != null) {
                Response response = gson.fromJson(responseString, Response.class);
                if (response.getSuccess()) {
                    List<Message> messages = gson.fromJson(response.getMessage(), new TypeToken<List<Message>>() {}.getType());
                    Platform.runLater(() -> displayMessages(messages));
                } else {
                    showAlert("Ошибка", response.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить сообщения: " + e.getMessage());
        }
    }

    private void displayMessages(List<Message> messages) {
        ObservableList<Message> sentMessages = FXCollections.observableArrayList();
        ObservableList<Message> receivedMessages = FXCollections.observableArrayList();

        User currentUser = CurrentUser.getInstance().getUser();

        for (Message message : messages) {
            if (message.getSender().getId() == currentUser.getId()) {
                sentMessages.add(message);
            } else {
                receivedMessages.add(message);
            }
        }

        sentMessagesListView.setItems(sentMessages);
        receivedMessagesListView.setItems(receivedMessages);
    }

    @FXML
    private void handleSendMessage() {
        User selectedUser = accountTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Ошибка", "Выберите получателя.");
            return;
        }

        String content = messageInput.getText().trim();
        if (content.isEmpty()) {
            showAlert("Ошибка", "Введите текст сообщения.");
            return;
        }

        User currentUser = CurrentUser.getInstance().getUser();
        Message message = new Message();
        message.setSender(currentUser);
        message.setReceiver(selectedUser);
        message.setContent(content);

        Request request = new Request();
        request.setRequestType(RequestType.SEND_MESSAGE);
        request.setMessage(gson.toJson(message));

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            messageInput.clear();
            showAlert("Успех", "Сообщение отправлено.");
            sentMessagesListView.getItems().add(message);
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось отправить сообщение: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        // Обработчик возврата в главное меню
    }
}
