package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.FixedAsset;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.example.client.Utility.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class CalculateAmortizationController {

    @FXML
    private Button calculateButton;

    @FXML
    private TableView<FixedAsset> depreciationTable;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button exportButton;

    @FXML
    private Button generateReportButton;

    @FXML
    private ComboBox<String> methodComboBox;

    @FXML
    private DatePicker startDatePicker;

    private ObservableList<FixedAsset> fixedAssets;
    Gson gson = new Gson();

    public CalculateAmortizationController() { }


    public void initialize() {
        // Инициализация данных и настройка таблицы
        setupTableColumns();
        loadFixedAssetsFromDatabase();

        // Заполнение ComboBox методами амортизации
        methodComboBox.setItems(FXCollections.observableArrayList("Линейный", "Метод уменьшаемого остатка", "Производственный"));

        // Обработка нажатия на кнопку "Рассчитать"
        calculateButton.setOnAction(event -> calculateDepreciation());
    }

    private void setupTableColumns() {
        TableColumn<FixedAsset, String> nameColumn = new TableColumn<>("Название ОС");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<FixedAsset, String> inventoryNumberColumn = new TableColumn<>("Инв. номер");
        inventoryNumberColumn.setCellValueFactory(new PropertyValueFactory<>("inventoryNumber"));

        TableColumn<FixedAsset, Double> initialCostColumn = new TableColumn<>("Стоимость (нач. п.)");
        initialCostColumn.setCellValueFactory(new PropertyValueFactory<>("initialCost"));

        TableColumn<FixedAsset, Double> residualValueColumn = new TableColumn<>("Ост. стоимость");
        residualValueColumn.setCellValueFactory(new PropertyValueFactory<>("residualValue"));

        TableColumn<FixedAsset, Double> depreciationColumn = new TableColumn<>("Амортизация (за период)");

        depreciationTable.getColumns().addAll(nameColumn, inventoryNumberColumn, initialCostColumn, residualValueColumn, depreciationColumn);
    }

    private void loadFixedAssetsFromDatabase() {
        try {
            // Создаем запрос на получение всех активов
            Request request = new Request(RequestType.GETALL_ASSET, null);
            String jsonRequest = gson.toJson(request);

            // Отправляем запрос на сервер
            ClientSocket.getInstance().getOut().println(jsonRequest);
            ClientSocket.getInstance().getOut().flush();

            // Обрабатываем ответ от сервера
            handleServerResponse();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при загрузке данных об основных средствах: " + e.getMessage());
        }
    }

    private void handleServerResponse() {
        try {
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseLine = in.readLine();

            if (responseLine != null) {
                Response response = gson.fromJson(responseLine, Response.class);

                if (response.getSuccess()) {
                    // Парсинг данных основных средств из JSON-ответа
                    Type listType = new TypeToken<List<FixedAsset>>() {}.getType();
                    List<FixedAsset> assets = gson.fromJson(response.getMessage(), listType);

                    // Обновляем таблицу с загруженными основными средствами
                    fixedAssets = FXCollections.observableArrayList(assets);
                    depreciationTable.setItems(fixedAssets);

                    System.out.println("Данные загружены успешно.");
                } else {
                    showAlert("Ошибка", "Ошибка от сервера: " + response.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при получении ответа от сервера: " + e.getMessage());
        }
    }

    private void calculateDepreciation() {
        String selectedMethod = methodComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (selectedMethod == null || startDate == null || endDate == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Пожалуйста, выберите метод амортизации и даты.");
            alert.show();
            return;
        }

        for (FixedAsset asset : fixedAssets) {
            double depreciationValue = 0;

            // Рассчитываем амортизацию в зависимости от метода
            switch (selectedMethod) {
                case "Линейный":
                    depreciationValue = calculateLinearDepreciation(asset, startDate, endDate);
                    break;
                case "Метод уменьшаемого остатка":
                    depreciationValue = calculateDecliningBalanceDepreciation(asset, startDate, endDate);
                    break;
                case "Производственный":
                    // depreciationValue = calculateProductionDepreciation(asset);
                    break;
            }

            // Здесь можно добавить расчетные значения в таблицу или обновить объект FixedAsset
            System.out.println("Амортизация для " + asset.getName() + ": " + depreciationValue);
        }
    }

    private double calculateLinearDepreciation(FixedAsset asset, LocalDate startDate, LocalDate endDate) {
        int years = endDate.getYear() - startDate.getYear();
        return (asset.getInitialCost() - asset.getResidualValue()) / asset.getUsefulLife() * years;
    }

    private double calculateDecliningBalanceDepreciation(FixedAsset asset, LocalDate startDate, LocalDate endDate) {
        double rate = 2.0 / asset.getUsefulLife(); // Коэффициент амортизации
        double depreciation = 0;
        double currentValue = asset.getInitialCost();

        for (int i = 0; i < endDate.getYear() - startDate.getYear(); i++) {
            depreciation += currentValue * rate;
            currentValue -= depreciation;
        }
        return depreciation;
    }

  /*  private double calculateProductionDepreciation(FixedAsset asset) {
        // Предполагается, что метод уже реализован, если необходимо
        double totalProduction = asset.getTotalProduction();  // общее количество единиц производства за весь срок
        double periodProduction = asset.getPeriodProduction();  // количество единиц производства за текущий период

        if (totalProduction == 0) {
            return 0;  // Избегаем деления на ноль
        }

        // Формула амортизации по производственному методу
        return (asset.getInitialCost() - asset.getResidualValue()) * (periodProduction / totalProduction);
    }*/

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
