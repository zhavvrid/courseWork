package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.DepreciationCalculation;
import com.example.client.Models.Entities.FixedAsset;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalculateAmortizationController {

    @FXML
    private TableColumn<DepreciationCalculation, String> nameColumn;
    @FXML
    private TableColumn<DepreciationCalculation, String> inventoryNumberColumn;
    @FXML
    private TableColumn<DepreciationCalculation, String> depreciationMethodColumn;
    @FXML
    private TableColumn<DepreciationCalculation, BigDecimal> initialCostColumn;
    @FXML
    private TableColumn<DepreciationCalculation, BigDecimal> depreciationAmountColumn;
    @FXML
    private TableColumn<DepreciationCalculation, BigDecimal> accumulatedDepreciationColumn;
    @FXML
    private TableColumn<DepreciationCalculation, BigDecimal> residualValueCol;
    @FXML
    private TableColumn<DepreciationCalculation, BigDecimal> residualValueColumn;
    @FXML
    private TableColumn<DepreciationCalculation, BigDecimal> usefulLifeColumn;
    @FXML
    private TableView<DepreciationCalculation> depreciationTable;
    @FXML
    private TableColumn<DepreciationCalculation, String> purchaseDateColumn;
    @FXML
    private TableColumn<DepreciationCalculation, String> depreciationCalculationDateColumn;
    @FXML
    private TableColumn<DepreciationCalculation, Number> rowNumberColumn;
    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;
    @FXML
    private BarChart<String, Number> depreciationBarChart;

    @FXML
    private LineChart<String, Number> depreciationLineChart;

    private FixedAsset selectedAsset;
    private final Gson gson = new Gson();
    private List<FixedAsset> assets = new ArrayList<>();
    @FXML
    void initialize(){
        rowNumberColumn.setCellValueFactory(column ->
                new ReadOnlyObjectWrapper<>(depreciationTable.getItems().indexOf(column.getValue()) + 1)
        );
    }
    public void setSelectedAsset(FixedAsset selectedAsset) {
        this.selectedAsset = selectedAsset;
        calculateAmortization();

    }
    public void displayAmortizationResults(JsonArray amortizationResults) {
        ObservableList<DepreciationCalculation> results = FXCollections.observableArrayList();
        for (JsonElement element : amortizationResults) {
            DepreciationCalculation result = gson.fromJson(element, DepreciationCalculation.class);
            results.add(result);
        }

        depreciationTable.setItems(results);
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFixedAsset().getName()));
        inventoryNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFixedAsset().getInventoryNumber()));
        depreciationMethodColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepreciationMethod()));
        initialCostColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getInitialCost()));
        usefulLifeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(BigDecimal.valueOf(cellData.getValue().getUsefulLife())));
        depreciationAmountColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDepreciationAmount()));
        accumulatedDepreciationColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAccumulatedDepreciation()));
        residualValueCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(BigDecimal.valueOf(cellData.getValue().getResidualValueFromAsset())));
        residualValueColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getResidualValue()));
        purchaseDateColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getPurchaseDate()));
        depreciationCalculationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCalculationDate()));
    }


    @FXML
    private void filterByDateRange() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null) {
            ObservableList<DepreciationCalculation> filteredResults = depreciationTable.getItems().filtered(
                    item -> {
                        LocalDate calculationDate = LocalDate.parse(item.getCalculationDate());
                        return !calculationDate.isBefore(startDate) && !calculationDate.isAfter(endDate);
                    }
            );
            depreciationTable.setItems(filteredResults);
        } else {
            showAlert("Ошибка", "Пожалуйста, выберите обе даты для фильтрации.");
        }
    }



    @FXML
    private void generateReport() {
        ObservableList<DepreciationCalculation> data = depreciationTable.getItems();
        if (data.isEmpty()) {
            showAlert("Ошибка", "Нет данных для генерации отчета.");
            return;
        }

        try {
            // Загрузка FXML для окна отчета
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportWindow.fxml"));
            AnchorPane reportRoot = loader.load();

            // Получение контроллера окна отчета
            ReportWindowController reportController = loader.getController();
            reportController.createChart(new ArrayList<>(data)); // Передаем данные для графика

            // Создание нового окна для отчета
            Stage reportStage = new Stage();
            reportStage.setTitle("Отчет по амортизации");
            reportStage.setScene(new Scene(reportRoot));
            reportStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно отчета: " + e.getMessage());
        }
    }




    @FXML
    public void calculateAmortization() {
        if (selectedAsset == null) {
            showAlert("Ошибка", "Не выбран актив для расчета.");
            return;
        }

        String selectedMethod = selectedAsset.getDepreciationMethod();
        JsonObject searchJson = new JsonObject();
        searchJson.addProperty("assetId", selectedAsset.getId());
        searchJson.addProperty("method", selectedMethod);

        Request request = new Request();
        request.setRequestType(RequestType.CALCULATE_AMORTIZATION);
        request.setMessage(searchJson.toString());

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
                Platform.runLater(() -> displayAmortizationResults(amortizationResults));
            } else {
                Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось выполнить расчет амортизации: " + e.getMessage());
        }
    }


    @FXML
    private void exportToExcel() {
        // Реализуйте логику экспорта данных в Excel
        showAlert("Информация", "Экспорт в Excel будет реализован позже.");
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
                Platform.runLater(() -> displayAmortizationResults(amortizationResults));
            } else {
                Platform.runLater(() -> showAlert("Ошибка", response.getMessage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось выполнить расчет амортизации: " + e.getMessage());
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
