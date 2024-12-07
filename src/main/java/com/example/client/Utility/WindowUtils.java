package com.example.client.Utility;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowUtils {

    // Method to center any stage
    public static void centerWindow(Stage stage) {

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double windowWidth = stage.getWidth();
        double windowHeight = stage.getHeight();


        double x = (screenBounds.getWidth() - windowWidth) / 2;
        double y = (screenBounds.getHeight() - windowHeight) / 2;

        stage.setX(x);
        stage.setY(y);
    }
}

