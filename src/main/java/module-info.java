module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires jakarta.xml.bind;
    requires java.desktop;
    requires java.xml.crypto;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.swing;

    opens com.example.client to javafx.fxml;
    exports com.example.client;

    opens com.example.client.Models.TCP to com.google.gson, jakarta.xml.bind;
    opens com.example.client.Enums to com.google.gson, jakarta.xml.bind;
    exports com.example.client.Models.Entities;
    opens com.example.client.Models.Entities to com.google.gson, jakarta.xml.bind, javafx.fxml;
}
