package com.example.client;
import com.example.client.Models.Entities.User;
import com.example.client.Utility.WindowUtils;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import com.example.client.Enums.RequestType;
import com.example.client.Models.TCP.Request;
import com.example.client.Models.TCP.Response;
import com.example.client.Utility.ClientSocket;
import com.google.gson.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.example.client.Models.Entities.DepreciationCalculation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;

public class ReportWindowController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    private final Gson gson = new Gson();

    public void initialize() {
        barChart.setVisible(true);
        lineChart.setVisible(false);
        pieChart.setVisible(false);
    }

    public void createChart(List<DepreciationCalculation> calculations) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Амортизация по активам");

        for (DepreciationCalculation calc : calculations) {
            String assetName = calc.getFixedAsset().getName();
            BigDecimal depreciationAmount = calc.getDepreciationAmount();
            series.getData().add(new XYChart.Data<>(assetName, depreciationAmount));
        }
        barChart.getData().add(series);


        createPieChart(calculations);

        createLineChart(calculations);
    }

    private void createPieChart(List<DepreciationCalculation> calculations) {
        Map<String, BigDecimal> categoryTotals = new HashMap<>();

        for (DepreciationCalculation calc : calculations) {
            String category = calc.getFixedAsset().getCategory();
            BigDecimal depreciationAmount = calc.getDepreciationAmount();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, BigDecimal.ZERO).add(depreciationAmount));
        }

        for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue().doubleValue());
            pieChart.getData().add(slice);
        }
    }

    private void createLineChart(List<DepreciationCalculation> calculations) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Накопленная амортизация по датам");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (DepreciationCalculation calc : calculations) {
            try {
                Date calculationDate = sdf.parse(calc.getCalculationDate());
                String formattedDate = sdf.format(calculationDate);
                BigDecimal accumulatedDepreciation = calc.getAccumulatedDepreciation();

                series.getData().add(new XYChart.Data<>(formattedDate, accumulatedDepreciation));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        lineChart.getData().add(series);
    }

    @FXML
    private void showBarChart() {
        fadeTransition(barChart, true);
        fadeTransition(lineChart, false);
        fadeTransition(pieChart, false);
    }

    @FXML
    private void showLineChart() {
        fadeTransition(barChart, false);
        fadeTransition(lineChart, true);
        fadeTransition(pieChart, false);
    }

    @FXML
    private void showPieChart() {
        fadeTransition(barChart, false);
        fadeTransition(lineChart, false);
        fadeTransition(pieChart, true);
    }

    private void fadeTransition(javafx.scene.Node node, boolean visible) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), node);
        if (visible) {
            node.setVisible(true);
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
        } else {
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
        }
        fadeTransition.play();
    }

    private static final String RESOURCES_PATH = "src/main/resources/reports/";

    /**
     * Сохраняет изображение графика в папку ресурсов.
     *
     * @param chart График, который нужно сохранить.
     * @param chartName Имя файла для изображения графика.
     */
    private byte[] saveChartAsImage(javafx.scene.Node chart, String chartName) {
        final byte[] imageData = new byte[0]; // Для правильного использования в lambda-выражениях
        try {
            // Проверяем, что график видим
            if (chart.isVisible()) {
                // Запускаем задачу на главном потоке JavaFX для корректного рендеринга
                Platform.runLater(() -> {
                    try {
                        // Даем графику время для отрисовки
                        Thread.sleep(200); // Задержка в 200 миллисекунд, можно увеличить, если график сложный

                        // Захватываем изображение графика
                        WritableImage writableImage = chart.snapshot(null, null);

                        // Формируем имя файла с временной меткой
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                        String fileName = RESOURCES_PATH + chartName + "_" + timestamp + ".png";
                        File file = new File(fileName);

                        // Преобразуем изображение в BufferedImage для записи
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        ImageIO.write(bufferedImage, "png", file); // Сохраняем файл на диск
                        System.out.println("Изображение графика успешно сохранено: " + file.getAbsolutePath());

                        // Также сохраняем изображение в байтовый массив для дальнейшего использования
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                        byte[] data = byteArrayOutputStream.toByteArray();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Ошибка при сохранении изображения графика: " + e.getMessage());
                    }
                });
            } else {
                System.out.println("Ошибка: График невидим, невозможно сохранить изображение.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при выполнении операции сохранения изображения: " + e.getMessage());
        }
        return imageData; // Возвращаем изображение как байтовый массив (если нужно отправить)
    }

    @FXML
    public void saveReport() {
        JsonObject reportJson = new JsonObject();

        String reportType = getReportType();
        reportJson.addProperty("report_type", reportType);

        JsonArray reportContent = generateReportContent();
        reportJson.add("content", reportContent);
        User currentUser = CurrentUser.getInstance().getUser();
        int currentUserId = currentUser.getId();
        reportJson.addProperty("created_by_user_id", currentUserId);

        byte[] imageData = null;
        if (barChart.isVisible()) {
            imageData = saveChartAsImage(barChart, reportType);
        } else if (lineChart.isVisible()) {
            imageData = saveChartAsImage(lineChart, reportType);
        } else if (pieChart.isVisible()) {
            imageData = saveChartAsImage(pieChart, reportType);
        }

        if (imageData != null) {
            String base64ImageData = Base64.getEncoder().encodeToString(imageData);
            reportJson.addProperty("image_data", base64ImageData);
        }

        Request request = new Request();
        request.setRequestType(RequestType.SAVE_REPORT);
        request.setMessage(reportJson.toString());

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            if (out == null) {
                throw new IOException("Output stream is not initialized.");
            }

            String jsonRequest = gson.toJson(request);
            System.out.println("Sending request: " + jsonRequest);
            out.println(jsonRequest);
            out.flush();
            BufferedReader in = ClientSocket.getInstance().getIn();
            String responseString = in.readLine();
            if (responseString == null) {
                throw new IOException("No response from server.");
            }

            System.out.println("Received response: " + responseString);
            Response response = gson.fromJson(responseString, Response.class);

            if (response.getSuccess()) {
                showAlert("Успех", "Отчет успешно сохранен.");
            } else {
                showAlert("Ошибка", response.getMessage());
            }

        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось сохранить отчет: " + e.getMessage());
        }
    }



    private void showAlert(String успех, String s) {
    }

    private String getReportType() {
        if (barChart.isVisible()) {
            return "BarChart";
        } else if (lineChart.isVisible()) {
            return "LineChart";
        } else if (pieChart.isVisible()) {
            return "PieChart";
        }
        return "Unknown";
    }
    private JsonArray generateReportContent() {
        JsonArray contentArray = new JsonArray();

        if (barChart.isVisible()) {
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                JsonObject seriesObject = new JsonObject();
                seriesObject.addProperty("name", series.getName());

                JsonArray dataPoints = new JsonArray();
                for (XYChart.Data<String, Number> data : series.getData()) {
                    JsonObject dataPoint = new JsonObject();
                    dataPoint.addProperty("category", data.getXValue());
                    dataPoint.addProperty("value", data.getYValue().doubleValue());
                    dataPoints.add(dataPoint);
                }
                seriesObject.add("data", dataPoints);
                contentArray.add(seriesObject);
            }
        } else if (lineChart.isVisible()) {
            for (XYChart.Series<String, Number> series : lineChart.getData()) {
                JsonObject seriesObject = new JsonObject();
                seriesObject.addProperty("name", series.getName());

                JsonArray dataPoints = new JsonArray();
                for (XYChart.Data<String, Number> data : series.getData()) {
                    JsonObject dataPoint = new JsonObject();
                    dataPoint.addProperty("date", data.getXValue());
                    dataPoint.addProperty("value", data.getYValue().doubleValue());
                    dataPoints.add(dataPoint);
                }
                seriesObject.add("data", dataPoints);
                contentArray.add(seriesObject);
            }
        } else if (pieChart.isVisible()) {
            for (PieChart.Data slice : pieChart.getData()) {
                JsonObject sliceObject = new JsonObject();
                sliceObject.addProperty("category", slice.getName());
                sliceObject.addProperty("value", slice.getPieValue());
                contentArray.add(sliceObject);
            }
        }

        return contentArray;
    }

    @FXML
    public void handleBack(ActionEvent actionEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CalculateAmortization.fxml"));
            Parent root = loader.load();

            // Получаем контроллер из FXML
            CalculateAmortizationController controller = loader.getController();

            // Загружаем амортизацию
            controller.loadAmortization();

            // Создаем и отображаем новое окно
            Stage previousStage = new Stage();
            previousStage.setScene(new Scene(root));
            previousStage.setTitle("Расчет амортизации основных средств");
            previousStage.show();

            // Центрируем окно
            WindowUtils.centerWindow(previousStage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть предыдущее меню: " + e.getMessage());
        }
    }

    public void closeWindow() {
        Stage stage = (Stage) barChart.getScene().getWindow();
        stage.close();
    }
}