package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.FixedAsset;
import com.example.client.Models.Entities.Role;
import com.example.client.Models.Entities.User;
import com.example.client.Models.TCP.Request;
import com.example.client.Utility.ClientSocket;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.PrintWriter;

public class EditUserController {

    @FXML
    private TextField email;

    @FXML
    private TextField login;

    @FXML
    private TextField password;

    @FXML
    private TextField role;
    private User user;
    private AccountManagement controller;

    public void setUser(User user, AccountManagement controller) {
        this.user = user;
        this.controller = controller;
        login.setText(user.getLogin());
        email.setText(user.getEmail());
        password.setText(user.getPassword());
        if (user.getRole() != null) {
            role.setText(user.getRole().getName());
        } else {
            role.setText("No role");
        }

    }

    @FXML
    private void cancel() {
        ((Stage) email.getScene().getWindow()).close();
    }


    private void sendUpdateRequest(User user) {
        Request request = new Request();
        request.setRequestType(RequestType.UPDATE_USER);
        request.setMessage(new Gson().toJson(user));

        PrintWriter out = ClientSocket.getInstance().getOut();
        out.println(new Gson().toJson(request));
        out.flush();
    }

    @FXML
    void saveUser(ActionEvent event) {
        try {
user.setId(user.getId());
            user.setLogin(login.getText());
            user.setEmail(email.getText());
            user.setPassword(password.getText());

            Role userRole = new Role();
            userRole.setName(role.getText());
            user.setRole(userRole);

            System.out.println("User: " + user);

            sendUpdateRequest(user);
            controller.loadUsers();

            ((Stage) email.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
