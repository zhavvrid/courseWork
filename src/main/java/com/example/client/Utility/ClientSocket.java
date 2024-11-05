package com.example.client.Utility;

import com.example.client.Models.Entities.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocket {
    private static final ClientSocket SINGLE_INSTANCE = new ClientSocket();
    private User user;
    private static Socket socket;
    private BufferedReader in;
    private PrintWriter out;

   private ClientSocket(){
       try {
           socket = new Socket("localhost",12355);
           in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           out = new PrintWriter(socket.getOutputStream());
       } catch (UnknownHostException e) {
           throw new RuntimeException(e);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }

   public static ClientSocket getInstance() {return SINGLE_INSTANCE;}

    public static Socket getSocket() {
        return socket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        } else {
            System.err.println("Ошибка: поток вывода не инициализирован.");
        }
    }

    public void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}
