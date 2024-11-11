package com.example.client;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void initialize() {
        // Изначально показываем BarChart, скрываем остальные
        barChart.setVisible(true);
        lineChart.setVisible(false);
        pieChart.setVisible(false);
    }

    public void createChart(List<DepreciationCalculation> calculations) {
        // Создаем BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Амортизация по активам");

        for (DepreciationCalculation calc : calculations) {
            String assetName = calc.getFixedAsset().getName();
            BigDecimal depreciationAmount = calc.getDepreciationAmount();
            series.getData().add(new XYChart.Data<>(assetName, depreciationAmount));
        }
        barChart.getData().add(series);

        // Создаем PieChart
        createPieChart(calculations);

        // Создаем LineChart по датам
        createLineChart(calculations);
    }

    private void createPieChart(List<DepreciationCalculation> calculations) {
        // Группировка по категориям активов
        Map<String, BigDecimal> categoryTotals = new HashMap<>();

        for (DepreciationCalculation calc : calculations) {
            String category = calc.getFixedAsset().getCategory();
            BigDecimal depreciationAmount = calc.getDepreciationAmount();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, BigDecimal.ZERO).add(depreciationAmount));
        }

        // Добавляем данные в PieChart
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
        // Анимация для плавного перехода
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

    // Функция для плавной анимации перехода видимости
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

    public void closeWindow() {
        Stage stage = (Stage) barChart.getScene().getWindow();
        stage.close();
    }
}
