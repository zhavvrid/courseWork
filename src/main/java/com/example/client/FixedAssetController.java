package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.FixedAsset;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FixedAssetController {

    @FXML
    private Button addButton;

    @FXML
    private TableView<FixedAsset> assetsTable;

    @FXML
    private TableColumn<FixedAsset, Integer> idColumn;

    @FXML
    private TableColumn<FixedAsset, String> nameColumn;

    @FXML
    private TableColumn<FixedAsset, String> inventoryNumberColumn;

    @FXML
    private TableColumn<FixedAsset, String> purchaseDateColumn;

    @FXML
    private TableColumn<FixedAsset, Double> initialCostColumn;

    @FXML
    private TableColumn<FixedAsset, Integer> usefulLifeColumn;

    @FXML
    private TableColumn<FixedAsset, Double> residualValueColumn;

    @FXML
    private TableColumn<FixedAsset, String> depreciationMethodColumn;

    @FXML
    private TableColumn<FixedAsset, String> categoryColumn;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> searchParameterComboBox;
    @FXML
    private ComboBox<String> sortParameterComboBox;
    @FXML
    private ComboBox<String> sortDirectionComboBox;
    @FXML
    private Button sortButton;

    private Gson gson = new Gson();

    @FXML
    private void initialize() {
        setupTableColumns();
        loadAssets();
        searchParameterComboBox.getItems().addAll("Название", "Инвентарный номер", "Категория", "Метод амортизации");
        searchParameterComboBox.setValue("Название");

        sortParameterComboBox.getItems().addAll("Название", "Начальная стоимость", "Срок полезного использования", "Ликвидационная стоимость", "Категория");
        sortParameterComboBox.setValue("Название");

        // Добавляем варианты направления сортировки
        sortDirectionComboBox.getItems().addAll("По возрастанию", "По убыванию");
        sortDirectionComboBox.setValue("По возрастанию");
    }

    @FXML
    private void openAddAssetWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddAsset.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Добавление основного средства");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAssets() {
        Request request = new Request();
        request.setRequestType(RequestType.GETALL_ASSET);

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();
            Response response = gson.fromJson(responseString, Response.class);

            System.out.println("Response string: " + responseString);

            if (response.getSuccess()) {
                // Извлекаем message
                String message = response.getMessage();

                // Пробуем разобрать message как JSON-строку, содержащую массив
                try {
                    List<FixedAsset> assets = gson.fromJson(message, new TypeToken<List<FixedAsset>>() {}.getType());
                    Platform.runLater(() -> assetsTable.getItems().setAll(assets));
                } catch (JsonSyntaxException e) {
                    // Обработка ошибки разбора JSON
                    Platform.runLater(() -> showAlert("Ошибка", "Неправильный формат данных: " + message));
                }
            } else {
                Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить активы: " + e.getMessage());
        }
    }


    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        inventoryNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInventoryNumber()));
        purchaseDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPurchaseDate()));
        initialCostColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getInitialCost()).asObject());
        usefulLifeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUsefulLife()).asObject());
        residualValueColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getResidualValue()).asObject());
        depreciationMethodColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepreciationMethod()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
    }


    @FXML
    private void editSelectedAsset() {
        FixedAsset selectedAsset = assetsTable.getSelectionModel().getSelectedItem();
        if (selectedAsset != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EditAsset.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Редактирование основного средства");

                // Pass the selected asset and this controller instance
                EditAssetController editController = loader.getController();
                editController.setAsset(selectedAsset, this); // Pass the current controller

                stage.setScene(new Scene(root));
                stage.showAndWait();
                // No need to call loadAssets() here anymore since it's done in EditAssetController
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось открыть окно редактирования.");
            }
        } else {
            showAlert("Ошибка", "Не выбран актив для редактирования.");
        }
    }


    @FXML
    private void deleteSelectedAsset() {
        FixedAsset selectedAsset = assetsTable.getSelectionModel().getSelectedItem();
        if (selectedAsset != null) {
            sendDeleteRequest(selectedAsset.getId());
            loadAssets(); // Refresh the asset list after deletion
        } else {
            showAlert("Ошибка", "Не выбран актив для удаления.");
        }
    }

    private void sendUpdateRequest(FixedAsset asset) {
        Request request = new Request();
        request.setRequestType(RequestType.UPDATE_ASSET);
        request.setMessage(gson.toJson(asset));

        PrintWriter out = ClientSocket.getInstance().getOut();
        out.println(gson.toJson(request));
        out.flush();
    }

    private void sendDeleteRequest(int assetId) {
        Request request = new Request();
        request.setRequestType(RequestType.DELETE_ASSET);
        request.setMessage(String.valueOf(assetId));

        PrintWriter out = ClientSocket.getInstance().getOut();
        out.println(gson.toJson(request));
        out.flush();
    }

    @FXML
    private void searchAssets() {
        String searchParameter = searchParameterComboBox.getValue(); // Получаем выбранный параметр
        String searchText = searchField.getText().trim(); // Получаем текст поиска

        if (searchText.isEmpty()) {
            showAlert("Ошибка", "Введите значение для поиска.");
            return;
        }

        // Создаем JSON-объект для параметров поиска
        JsonObject searchJson = new JsonObject();
        searchJson.addProperty("parameter", searchParameter);
        searchJson.addProperty("value", searchText);

        // Создаем запрос на сервер, устанавливаем тип запроса и сообщение с параметрами поиска
        Request request = new Request();
        request.setRequestType(RequestType.SEARCH_ASSET);
        request.setMessage(searchJson.toString()); // Передаем параметры поиска в формате JSON

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();
            Response response = gson.fromJson(responseString, Response.class);

            if (response.getSuccess()) {
                // Обрабатываем полученные результаты
                List<FixedAsset> filteredAssets = gson.fromJson(response.getMessage(), new TypeToken<List<FixedAsset>>() {}.getType());
                Platform.runLater(() -> assetsTable.getItems().setAll(filteredAssets));
            } else {
                Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось выполнить поиск: " + e.getMessage());
        }
    }

    @FXML
    private void sortAssets() {
        String sortParameter = sortParameterComboBox.getValue();
        String sortDirection = sortDirectionComboBox.getValue();

        JsonObject sortJson = new JsonObject();
        sortJson.addProperty("parameter", sortParameter);
        sortJson.addProperty("direction", sortDirection.equals("По возрастанию") ? "ASC" : "DESC");

        Request request = new Request();
        request.setRequestType(RequestType.SORT_ASSET);
        request.setMessage(sortJson.toString());

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();
            Response response = gson.fromJson(responseString, Response.class);

            if (response.getSuccess()) {
                List<FixedAsset> sortedAssets = gson.fromJson(response.getMessage(), new TypeToken<List<FixedAsset>>() {}.getType());
                Platform.runLater(() -> assetsTable.getItems().setAll(sortedAssets));
            } else {
                Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось выполнить сортировку: " + e.getMessage());
        }
    }
     @FXML
    private void openDepreciationCalculationWindow() {
        FixedAsset selectedAsset = assetsTable.getSelectionModel().getSelectedItem();

        if (selectedAsset == null) {
            showAlert("Ошибка", "Пожалуйста, выберите актив для расчета амортизации.");
            return;
        }

        String selectedMethod = selectedAsset.getDepreciationMethod();

        try {
            // Загружаем FXML для окна расчета амортизации
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DepreciationCalculationWindow.fxml"));
            Parent root = loader.load();

            // Получаем контроллер окна расчета амортизации
            DepreciationCalculationController controller = loader.getController();

            // Передаем метод амортизации в контроллер второго окна
            controller.setDepreciationMethod(selectedMethod);

            // Настраиваем новое окно
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Расчет амортизации");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно расчета амортизации.");
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
