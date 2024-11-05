package com.example.client;

import com.example.client.Utility.ClientSocket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ClientSocket.getInstance().getSocket();
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/client/main.fxml"));
        stage.setTitle("Учет амортизации основных средств");
        stage.setScene(new Scene(root, 700, 400));
        stage.show();

    }
    public static void main(String[] args) {launch(args);}
}
