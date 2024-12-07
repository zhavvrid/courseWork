package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.AmortizationResult;
import com.example.client.Models.Entities.DepreciationCalculation;
import com.example.client.Models.Entities.FixedAsset;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.example.client.Utility.WindowUtils;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private Button backButton;

    @FXML
    private Button calculateButton;
    @FXML
    private Button viewAmortizationAssets;
    @FXML
    private Button viewAssets;

    @FXML
    private ComboBox<String> searchParameterComboBox;
    @FXML
    private Button sortButton;

    private Gson gson = new Gson();

    @FXML
    private void initialize() {
        setupTableColumns();
        loadAssets();

        searchParameterComboBox.getItems().addAll("Название", "Инвентарный номер", "Категория", "Метод амортизации");
        searchParameterComboBox.setValue("Название");
    }

    @FXML
    private void openAddAssetWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddAsset.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Добавление основного средства");
            stage.setOnHidden(e -> loadAssets());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAssets() {
        ObservableList<FixedAsset> assetsList = FXCollections.observableArrayList();
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
                String message = response.getMessage();
                try {
                    List<FixedAsset> assets = gson.fromJson(message, new TypeToken<List<FixedAsset>>() {
                    }.getType());
                    Platform.runLater(() -> {
                        assetsTable.getItems().clear();
                        assetsTable.getItems().addAll(assets);
                        assetsList.setAll(assets);

                    });
                } catch (JsonSyntaxException e) {
                    Platform.runLater(() -> showAlert("Уведомление",  message));
                }
            } else {
                Platform.runLater(() -> showAlert("Уведомление", response.getMessage()));
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

    public void updateAssetInTable(FixedAsset updatedAsset) {
        ObservableList<FixedAsset> items = assetsTable.getItems();
        for (int i = 0; i < items.size(); i++) {
            FixedAsset asset = items.get(i);
            if (asset.getId() == updatedAsset.getId()) {
                // Обновляем данные в таблице
                items.set(i, updatedAsset);
                break;
            }
        }
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

                EditAssetController editController = loader.getController();
                editController.setAsset(selectedAsset, this);
                stage.setOnHidden(e -> loadAssets());
                stage.setScene(new Scene(root));
                stage.showAndWait();
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
            Platform.runLater(() -> {
                assetsTable.getItems().remove(selectedAsset);
                loadAssets();
            });

        } else {
            showAlert("Ошибка", "Не выбран актив для удаления.");
        }
    }

    private void sendDeleteRequest(int assetId) {
        Request request = new Request();
        request.setRequestType(RequestType.DELETE_ASSET);
        request.setMessage(String.valueOf(assetId));

        PrintWriter out = ClientSocket.getInstance().getOut();
        out.println(gson.toJson(request));
        out.flush();
    }

    private void sendUpdateRequest(FixedAsset asset) {
        Request request = new Request();
        request.setRequestType(RequestType.UPDATE_ASSET);
        request.setMessage(gson.toJson(asset));

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

        JsonObject searchJson = new JsonObject();
        searchJson.addProperty("parameter", searchParameter);
        searchJson.addProperty("value", searchText);

        Request request = new Request();
        request.setRequestType(RequestType.SEARCH_ASSET);
        request.setMessage(searchJson.toString());

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();
            Response response = gson.fromJson(responseString, Response.class);

            if (response.getSuccess()) {
                List<FixedAsset> filteredAssets = gson.fromJson(response.getMessage(), new TypeToken<List<FixedAsset>>() {
                }.getType());
                Platform.runLater(() -> assetsTable.getItems().setAll(filteredAssets));
            } else {
                Platform.runLater(() -> showAlert("Уведомление", response.getMessage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось выполнить поиск: " + e.getMessage());
        }
    }


    @FXML
    private void openDepreciationCalculationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CalculateAmortization.fxml"));
            Parent root = loader.load();
            FixedAsset selectedAsset = assetsTable.getSelectionModel().getSelectedItem();
            if (selectedAsset == null) {
                showAlert("Ошибка", "Необходимо выбрать актив для расчета амортизации.");
                return;
            }

            CalculateAmortizationController controller = loader.getController();
            controller.setSelectedAsset(selectedAsset);

            Stage stage = (Stage) calculateButton.getScene().getWindow();
            stage.setTitle("Расчет амортизации");
            stage.setScene(new Scene(root));
            stage.show();
            WindowUtils.centerWindow(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void loadAmortization() {

        Request request = new Request();
        request.setRequestType(RequestType.GETALL_AMORTIZATION);

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();
            Response response = gson.fromJson(responseString, Response.class);
            System.out.println("Response string: " + responseString);

            if (response.getSuccess()) {
                JsonArray amortizationResults = gson.fromJson(response.getMessage(), JsonArray.class);
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("CalculateAmortization.fxml"));
                        Parent root = loader.load();

                        CalculateAmortizationController calculateAmortizationController = loader.getController();

                        calculateAmortizationController.displayAmortizationResults(amortizationResults);
                        Stage stage = (Stage) viewAmortizationAssets.getScene().getWindow();
                        stage.setTitle("Результаты амортизации");
                        stage.setScene(new Scene(root));
                        stage.show();
                        WindowUtils.centerWindow(stage);

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Ошибка", "Не удалось загрузить окно амортизации.");
                    }
                });
            } else {
                Platform.runLater(() -> showAlert("Уведомление", response.getMessage()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            WindowUtils.centerWindow(stage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть предыдущее меню: " + e.getMessage());
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
