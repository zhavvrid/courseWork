package com.example.client;

        import com.example.client.Enums.RequestType;
        import com.example.client.Models.Entities.FixedAsset;
        import com.example.client.Models.TCP.Request;
        import com.example.client.Models.TCP.Response;
        import com.example.client.Utility.ClientSocket;
        import com.google.gson.Gson;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.fxml.FXML;
        import javafx.scene.control.Alert;
        import javafx.scene.control.Button;
        import javafx.scene.control.ComboBox;
        import javafx.scene.control.DatePicker;
        import javafx.scene.control.TextField;
        import javafx.scene.layout.AnchorPane;

        import java.io.BufferedReader;
        import java.io.IOException;

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

    private Gson gson = new Gson();

    @FXML
    public void initialize() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Компьютер", "Мебель", "Транспорт", "Оборудование"
        );
        categoryComboBox.setItems(categories);

        ObservableList<String> depreciationMethods = FXCollections.observableArrayList(
                "Линейный", "Ускоренный", "Производственный"
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

        return new FixedAsset(0, name, inventoryNumber, purchaseDate, initialCost, usefulLife, residualValue, depreciationMethod, category);
    }

    private void saveAssetToDatabase(FixedAsset asset) throws Exception {
        // Create the request to save the asset
        Request request = new Request();
        request.setRequestType(RequestType.ADD_ASSET);
        request.setMessage(new Gson().toJson(asset));

        String jsonRequest = new Gson().toJson(request); // Делаем JSON-строку из Request
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
                showAlert("Ответ от сервера", response.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
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

