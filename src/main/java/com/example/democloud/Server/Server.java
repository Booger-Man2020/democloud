package com.example.democloud.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static DataInputStream in;
    public static DataOutputStream out;

    public Server() throws IOException {
        try (ServerSocket server = new ServerSocket(8180)) {
            System.out.println("Server started");
            while (true) {
                Socket socket = server.accept();
                Handler handler = new Handler(socket);
                new Thread(handler).start();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
