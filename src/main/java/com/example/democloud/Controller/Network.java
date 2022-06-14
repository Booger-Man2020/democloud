package com.example.democloud.Controller;

import java.io.*;
import java.net.Socket;

public class Network {
    public static DataInputStream in;
    public static DataOutputStream out;
    private Socket socket;

    public Network(int port) throws IOException {
        socket = new Socket("localhost", port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public String readString() throws IOException {
        return in.readUTF();
    }

    public int readInt() throws IOException {
        return in.readInt();
    }

    public DataOutput getOs() {
        return out;
    }

    public DataInput getIs() {
        return in;

    }

    public void writeMessage(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }
}
