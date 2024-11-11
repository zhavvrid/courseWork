package com.example.server;

import com.example.server.Utility.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final int PORT = 12355;
    private static ServerSocket serverSocket;

    private static ClientThread clientHandler;
    private static Thread thread;
    private static List<Socket> currentSockets = new ArrayList<Socket>();


    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT);
        while (true) {
            for (Socket socket: currentSockets) {
                if(socket.isClosed()) {
                    currentSockets.remove(socket);
                    continue;
                }
                System.out.println("Клиент: " + socket.getInetAddress().getHostAddress());
            }
            Socket socket = serverSocket.accept();
            currentSockets.add(socket);
            clientHandler = new ClientThread(socket);
            thread = new Thread(clientHandler);
            thread.start();
            System.out.flush();

        }
    }

    protected void finalize() throws IOException {
        serverSocket.close();
    }
}
