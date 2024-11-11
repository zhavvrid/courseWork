package com.example.client;

        import com.example.client.Enums.RequestType;
        import com.example.client.Models.Entities.FixedAsset;
        import com.example.client.Models.TCP.Request;
        import com.example.client.Models.TCP.Response;
        import com.example.client.Utility.ClientSocket;
        import com.google.gson.Gson;
        import com.google.gson.JsonArray;
        import com.google.gson.JsonObject;
        import javafx.application.Platform;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.Alert;
        import javafx.scene.control.Button;
        import javafx.scene.control.ComboBox;
        import javafx.scene.control.DatePicker;
        import javafx.scene.control.TextField;
        import javafx.scene.layout.AnchorPane;
        import javafx.stage.Stage;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.PrintWriter;

public class AddAssetController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField inventoryNumberField;
    @FXML
    private DatePicker purchaseDatePicker;
    @FXML
    private TextField initialCostField;
    @FXML
    private TextField usefulLifeField;
    @FXML
    private TextField residualValueField;
    @FXML
    private ComboBox<String> depreciationMethodComboBox;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button clearButton;
    private FixedAsset selectedAsset;

    private CalculateAmortizationController calculateAmortizationController;
    public void setCalculateAmortizationController(CalculateAmortizationController calcController) {
        this.calculateAmortizationController = calcController;
        System.out.println("CalculateAmortizationController установлен: " + (calcController != null));
    }



    private Gson gson = new Gson();

    @FXML
    public void initialize() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Компьютер", "Мебель", "Транспорт", "Оборудование"
        );
        categoryComboBox.setItems(categories);

        ObservableList<String> depreciationMethods = FXCollections.observableArrayList(
                "Линейный", "Ускоренный", "Метод остаточной стоимости"
        );
        depreciationMethodComboBox.setItems(depreciationMethods);
    }

    @FXML
    private void handleSaveButtonAction() {
        try {
            FixedAsset asset = createFixedAssetFromInput();
            saveAssetToDatabase(asset);
            showAlert("Успех", "Актив успешно сохранен.");
            clearFields();
            // Navigate to the new panel
            navigateToNewPanel();
        } catch (Exception e) {
            showAlert("Ошибка", "Ошибка при сохранении актива: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearButtonAction() {
        clearFields();
    }

    private FixedAsset createFixedAssetFromInput() {
        String name = nameField.getText();
        String inventoryNumber = inventoryNumberField.getText();
        String purchaseDate = purchaseDatePicker.getValue().toString(); // Ensure it's in the correct format
        double initialCost = Double.parseDouble(initialCostField.getText());
        int usefulLife = Integer.parseInt(usefulLifeField.getText());
        double residualValue = Double.parseDouble(residualValueField.getText());
        String depreciationMethod = depreciationMethodComboBox.getValue();
        String category = categoryComboBox.getValue();

        return new FixedAsset(name, inventoryNumber, purchaseDate, initialCost, usefulLife, residualValue, depreciationMethod, category);
    }

    private void saveAssetToDatabase(FixedAsset asset) throws Exception {
        System.out.println("Перед отправкой: id = " + asset.getId());

        Request request = new Request();
        request.setRequestType(RequestType.ADD_ASSET);
        request.setMessage(new Gson().toJson(asset));

        String jsonRequest = new Gson().toJson(request); // Делаем JSON-строку из Request
        ClientSocket.getInstance().getOut().println(jsonRequest);
        ClientSocket.getInstance().getOut().flush();

        handleServerResponse(asset);

        System.out.println("После ответа от сервера: id = " + asset.getId());
    }

    private void handleServerResponse(FixedAsset asset) {
        try {
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseLine = in.readLine();
            if (responseLine != null) {
                Response response = new Gson().fromJson(responseLine, Response.class);
                if (response.getSuccess()) {
                    // Убедитесь, что id корректно передается в ответе
                    asset.setId(response.getId()); // Устанавливаем id из ответа
                    System.out.println("Получено id от сервера: " + asset.getId());
                }
                showAlert("Ответ от сервера", response.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
        }
    }

    private void calculateDepreciation(FixedAsset asset) throws Exception {
        // Create a custom object to send the assetId and depreciation method
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("assetId", asset.getId());
        requestObject.addProperty("method", depreciationMethodComboBox.getValue());

        Request request = new Request();
        request.setRequestType(RequestType.CALCULATE_AMORTIZATION);
        request.setMessage(requestObject.toString());

        String jsonRequest = new Gson().toJson(request);
        ClientSocket.getInstance().getOut().println(jsonRequest);
        ClientSocket.getInstance().getOut().flush();

        handleAmortizationResponse();
    }

    private void handleAmortizationResponse() {
        try {

            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseLine = in.readLine();
            if (responseLine != null) {
                Response response = new Gson().fromJson(responseLine, Response.class);
                if (response.getSuccess()) {
                    JsonArray amortizationResults = new Gson().fromJson(response.getMessage(), JsonArray.class);
                    showAlert("Ответ от сервера", response.getMessage());

                    // Pass the result to CalculateAmortizationController
                    CalculateAmortizationController calcController = new CalculateAmortizationController();
                    calcController.setSelectedAsset(selectedAsset);
                    calcController.displayAmortizationResults(amortizationResults);
                } else {
                    showAlert("Ошибка", "Ошибка при расчете амортизации: " + response.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
        }
    }

    @FXML
    private void handleCalculateDepreciationButtonAction() throws Exception {
        FixedAsset asset = createFixedAssetFromInput();
        saveAssetToDatabase(asset);
        System.out.println(asset);

        String selectedMethod = asset.getDepreciationMethod();

        // Формируем объект запроса для отправки данных на сервер
        JsonObject searchJson = new JsonObject();
        searchJson.addProperty("assetId", asset.getId());
        searchJson.addProperty("method", selectedMethod);

        Request request = new Request();
        request.setRequestType(RequestType.CALCULATE_AMORTIZATION);
        request.setMessage(searchJson.toString());

        try {
            // Отправляем запрос на сервер для расчета амортизации
            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(gson.toJson(request));
            out.flush();

            // Читаем и обрабатываем ответ от сервера
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();
            Response response = gson.fromJson(responseString, Response.class);

            if (response.getSuccess()) {
                JsonArray amortizationResults = gson.fromJson(response.getMessage(), JsonArray.class);
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("CalculateAmortization.fxml"));
                        Parent root = loader.load();

                        CalculateAmortizationController calculateAmortizationController = loader.getController();

                        calculateAmortizationController.displayAmortizationResults(amortizationResults);

                        Stage stage = new Stage();
                        stage.setTitle("Результаты амортизации");
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Ошибка", "Не удалось загрузить окно амортизации.");
                    }
                });
            } else {
                Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void clearFields() {
        nameField.clear();
        inventoryNumberField.clear();
        purchaseDatePicker.setValue(null);
        initialCostField.clear();
        usefulLifeField.clear();
        residualValueField.clear();
        depreciationMethodComboBox.getSelectionModel().clearSelection();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    private void navigateToNewPanel() {
        // Logic to switch to another panel, e.g., using a CardLayout or changing the Scene
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}