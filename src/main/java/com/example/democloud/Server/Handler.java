package com.example.democloud.Server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Handler implements Runnable {
    private final String serverDir = "server_files";
    private DataInputStream is;
    private DataOutputStream os;
    public Path serverDirectory;

    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client accepted");
     //   Files.createDirectory(Path.of("serverDir", "serverDir1"));
       // sendListOfFiles(serverDir);
    }


    private void sendListOfFiles(String dir) throws IOException {
        os.writeUTF("#list#");
        List<String> files = getFiles(serverDir);
        os.writeInt(files.size());
        for (String file : files) {
            os.writeUTF(file);
        }
        os.flush();
    }

    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        assert list != null;
        if (list == null){
            list= new File(dir).list();
        }
        return Arrays.asList(list);
    }

    @Override
    public void run() {
        byte[] buf = new byte[256];
        try {
            while (true) {
                String command = is.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#file#")) {
                    String fileName = is.readUTF();
                    long len = is.readLong();
                    serverDirectory = Files.createDirectory(Path.of("serverDir", "serverDir1"));
                    File file = Path.of(serverDir).resolve(fileName).toFile();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        for (int i = 0; i < (len + 255) / 256; i++) {
                            int read = is.read(buf);
                            fos.write(buf, 0, read);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendListOfFiles(serverDir);
                }
            }
        } catch (Exception e) {
            System.err.println("Connection was broken");
        }
    }
}

