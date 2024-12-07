    package com.example.client;

    import com.example.client.Enums.RequestType;
    import com.example.client.Models.Entities.User;
    import com.example.client.Models.TCP.Request;
    import com.example.client.Models.TCP.Response;
    import com.example.client.Utility.ClientSocket;
    import com.google.gson.Gson;
    import com.google.gson.JsonSyntaxException;
    import com.google.gson.reflect.TypeToken;
    import javafx.application.Platform;
    import javafx.beans.property.SimpleStringProperty;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.stage.Stage;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.PrintWriter;
    import java.util.List;

    public class AccountManagement {

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
private Button backButton;
        @FXML
        private TextField usernameField;
        @FXML
        private TextField loginField;
        @FXML
        private TextField passwordField;
        @FXML
        private TextField emailField;
        private Gson gson = new Gson();


        @FXML
        private void initialize() {

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
            passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            roleColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getRole().getName())
            );
            loadUsers();

        }

        @FXML
        private void handleAddAccount() {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("AddUser.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Добавление пользователя");
                    stage.setScene(new Scene(root));
                    stage.showAndWait();
                    loadUsers();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        @FXML
        private void handleEditAccount() {
                User selectedUser = accountTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    int userId = selectedUser.getId();
                    System.out.println("Selected User ID: " + userId);
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditUser.fxml"));
                        Parent root = loader.load();
                        EditUserController editController = loader.getController();

                        editController.setUser(selectedUser, this);
                        Stage stage = new Stage();
                        stage.setTitle("Редактирование пользователя");

                        stage.setScene(new Scene(root));
                        stage.showAndWait();
                        loadUsers();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Ошибка", "Не удалось открыть окно редактирования.");
                    }
                } else {
                    showAlert("Ошибка", "Не выбран актив для редактирования.");
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
                            Platform.runLater(() -> showAlert("Уведомление",  message));
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


        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        @FXML
        private void handleDeleteAccount() {
            User selectedUser = accountTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                Request request = new Request();
                request.setRequestType(RequestType.DELETE_USER);
                request.setMessage(String.valueOf(selectedUser.getId()));

                try {
                    PrintWriter out = ClientSocket.getInstance().getOut();
                    out.println(gson.toJson(request));
                    out.flush();

                    BufferedReader in = ClientSocket.getInstance().getIn();
                    String responseString = in.readLine();

                    if (responseString != null) {
                        Response response = gson.fromJson(responseString, Response.class);
                        if (response.getSuccess()) {
                            Platform.runLater(() -> {
                                loadUsers(); // Обновляем таблицу только после успешного удаления
                                showAlert("Успех", "Пользователь успешно удален.");
                            });
                        } else {
                            Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
                        }
                    } else {
                        Platform.runLater(() -> showAlert("Ошибка", "Сервер не вернул ответ."));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Ошибка", "Не удалось удалить пользователя: " + e.getMessage());
                }
            } else {
                showAlert("Ошибка", "Не выбран пользователь для удаления.");
            }
        }

        private void sendDeleteRequest(int userId) {
            Request request = new Request();
            request.setRequestType(RequestType.DELETE_USER);
            request.setMessage(String.valueOf(userId));

            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();
        }


        @FXML
        private void handleClearFields() {
            usernameField.clear();
            loginField.clear();
            passwordField.clear();
            emailField.clear();
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

    }
